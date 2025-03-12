package ZoneZone.com.accountHandler;

import ZoneZone.com.itemsHandler.UserItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class AccountController {

    private final String IMAGE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/";
    private final String PROFILE_DIRECTORY = "/home/jsheets1/ZoneZoneImages/profilePictures/";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg");

    private final AccountRepository myAccountRepository;
    private final Map<Long, AccountPlayTime> sessionPlayTimeCache = new HashMap<>();


    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.myAccountRepository = accountRepository;
    }

    // POST: /accountUsers/createUser
    @PostMapping("/accountUsers/createUser")
    public ResponseEntity<?> createUser(@RequestBody AccountModel account) {
        try {
            // ✅ Check if the username or email already exists (excluding default values)
            if (!account.getAccountUsername().equalsIgnoreCase("defaultUsername") &&
                    myAccountRepository.existsByAccountUsername(account.getAccountUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Username already exists"));
            }

            if (!account.getAccountEmail().equalsIgnoreCase("defaultEmail@gmail.com") &&
                    myAccountRepository.existsByAccountEmail(account.getAccountEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already exists"));
            }

            // ✅ Save the new account
            AccountModel savedAccount = myAccountRepository.save(account);
            return ResponseEntity.ok(savedAccount);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating user", "details", e.getMessage()));
        }
    }

    // DELETE: /accountUsers/deleteUser/{userID}
    @DeleteMapping("/accountUsers/deleteUser/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userID) {
        Optional<AccountModel> userOptional = myAccountRepository.findById(userID);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        AccountModel userToDelete = userOptional.get();

        // ✅ Remove the user from all friends lists
        List<AccountModel> allUsers = myAccountRepository.findAll();
        for (AccountModel account : allUsers) {
            if (account.getFriendsList().contains(userID)) {
                account.getFriendsList().remove(userID);
                myAccountRepository.save(account);
            }
        }

        // ✅ Remove the user from all blocked lists
        for (AccountModel account : allUsers) {
            if (account.getBlockedList().contains(userID)) {
                account.getBlockedList().remove(userID);
                myAccountRepository.save(account);
            }
        }

        // ✅ Delete the user from the database
        myAccountRepository.deleteById(userID);

        return ResponseEntity.ok().body(Map.of("message", "User deleted successfully"));
    }

    // UPDATE: /accountUsers/updateUser/{userID}
    @PutMapping("/accountUsers/updateUser/{userID}")
    public ResponseEntity<?> updateUser(@PathVariable Long userID, @RequestBody AccountModel updatedAccount) {
        Optional<AccountModel> accountOptional = myAccountRepository.findById(userID);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + userID + " not found.");
        }

        AccountModel account = accountOptional.get();
        Boolean currentOnlineStatus = account.getIsOnline();
        String currentAcountType = account.getAccountType();

        try {
            // ✅ Apply updates to account fields
            if (updatedAccount.getAccountUsername() != null) {
                account.setAccountUsername(updatedAccount.getAccountUsername());
            }
            if (updatedAccount.getAccountPassword() != null) {
                account.setAccountPassword(updatedAccount.getAccountPassword());
            }
            if (updatedAccount.getAccountEmail() != null) {
                account.setAccountEmail(updatedAccount.getAccountEmail());
            }
            if (updatedAccount.getFirstName() != null) {
                account.setFirstName(updatedAccount.getFirstName());
            }
            if (updatedAccount.getLastName() != null) {
                account.setLastName(updatedAccount.getLastName());
            }
            if (updatedAccount.getUserBirthday() != null) {
                account.setUserBirthday(updatedAccount.getUserBirthday());
            }
            if (updatedAccount.getAccountType() != null) {
                account.setAccountType(updatedAccount.getAccountType());
                currentAcountType =  updatedAccount.getAccountType();
            }
            if(updatedAccount.getIsBanned() != null) {
                if (currentAcountType.compareTo("Admin") != 0) {
                    account.setIsBanned(updatedAccount.getIsBanned());
                }
            }
            if(updatedAccount.getUserLevel() != null) {
                account.setUserLevel(updatedAccount.getUserLevel());
            }
            if(updatedAccount.getCurrentLevelXP() != null) {
                account.setCurrentLevelXP(updatedAccount.getCurrentLevelXP());
            }
            if(updatedAccount.getGemBalance() != null) {
                account.setGemBalance(updatedAccount.getGemBalance());
            }

            if (updatedAccount.getIsOnline() != null) {
                account.setIsOnline(updatedAccount.getIsOnline());
                currentOnlineStatus = updatedAccount.getIsOnline();
                if (!currentOnlineStatus) {
                    sessionPlayTimeCache.remove(userID);
                }
            }
            if(updatedAccount.getNumKills() != null) {
                account.setNumKills(updatedAccount.getNumKills());
            }
            if(updatedAccount.getNumDeaths() != null) {
                account.setNumDeaths(updatedAccount.getNumDeaths());
            }
            account.setKillDeathRatio();

            if (updatedAccount.getIsPlaying() != null) {
                if (Boolean.TRUE.equals(currentOnlineStatus)) {
                    account.setIsPlaying(updatedAccount.getIsPlaying());
                    if (updatedAccount.getIsPlaying()) {
                        account.setIsInQueue(false);
                    }
                }
                else {
                    account.setIsPlaying(false);
                }
            }
            if (updatedAccount.getIsInQueue() != null) {
                if (Boolean.TRUE.equals(currentOnlineStatus)) {
                    account.setIsInQueue(updatedAccount.getIsInQueue());
                    if (updatedAccount.getIsInQueue()) {
                        account.setIsPlaying(false);
                    }
                }
                else {
                    account.setIsInQueue(false);
                }
            }
            if (updatedAccount.getTotalUserPlayTime() != null) {
                account.getTotalUserPlayTime().setTime(
                        updatedAccount.getTotalUserPlayTime().getDays(),
                        updatedAccount.getTotalUserPlayTime().getHours(),
                        updatedAccount.getTotalUserPlayTime().getMinutes(),
                        updatedAccount.getTotalUserPlayTime().getSeconds()
                );
            }

            // ✅ Save persistent data
            myAccountRepository.save(account);

            // ✅ Inject session playtime from cache
            Map<String, Object> userData = getUserData(account);
            if (sessionPlayTimeCache.containsKey(userID)) {
                userData.put("userSessionPlayTime", sessionPlayTimeCache.get(userID));
            }

            return ResponseEntity.ok(userData);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    private LinkedHashMap<String, Object> getUserData(AccountModel account) {
        LinkedHashMap<String, Object> userData = new LinkedHashMap<>(); // Use LinkedHashMap to maintain insertion order

        userData.put("accountID", account.getAccountID());
        userData.put("accountUsername", account.getAccountUsername());
        userData.put("accountPassword", account.getAccountPassword());
        userData.put("accountEmail", account.getAccountEmail());
        userData.put("firstName", account.getFirstName());
        userData.put("lastName", account.getLastName());
        userData.put("userBirthday", account.getUserBirthday());
        userData.put("userAge", account.getUserAge());
        userData.put("accountType", account.getAccountType());
        userData.put("isBanned", account.getIsBanned());
        userData.put("profilePicturePath", account.getProfilePicturePath());
        userData.put("isOnline", account.getIsOnline());
        userData.put("isPlaying", account.getIsPlaying());
        userData.put("isInQueue", account.getIsInQueue());
        userData.put("userLevel", account.getUserLevel());
        userData.put("currentLevelXP", account.getCurrentLevelXP());
        userData.put("gemBalance", account.getGemBalance());
        userData.put("numKills", account.getNumKills());
        userData.put("numDeaths", account.getNumDeaths());
        userData.put("killDeathRatio", account.getKillDeathRatio());
        userData.put("friendsList", account.getFriendsList());
        userData.put("blockedList", account.getBlockedList());
        userData.put("ownedPlayerItems", account.getOwnedPlayerItems());
        userData.put("totalUserPlayTime", account.getTotalUserPlayTime());
        userData.put("userSessionPlayTime", account.getUserSessionPlayTime());

        return userData;
    }

    // GET: /accountUsers/listUsers - List all accounts
    @GetMapping("/accountUsers/listUsers")
    public ResponseEntity<List<LinkedHashMap<String, Object>>> listUsers() {
        List<AccountModel> accounts = myAccountRepository.findAll();
        List<LinkedHashMap<String, Object>> userList = new ArrayList<>();

        for (AccountModel account : accounts) {
            LinkedHashMap<String, Object> userData = getUserData(account);

            // ✅ Inject cached session playtime
            if (sessionPlayTimeCache.containsKey(account.getAccountID())) {
                userData.put("userSessionPlayTime", sessionPlayTimeCache.get(account.getAccountID()));
            }

            userList.add(userData);
        }

        return ResponseEntity.ok(userList);
    }

    // GET: /accountUsers/listUser/{userID} - Get one account by ID
    @GetMapping("/accountUsers/listUser/{id}")
    public ResponseEntity<Map<String, Object>> listUserById(@PathVariable Long id) {
        Optional<AccountModel> accountOpt = myAccountRepository.findById(id);

        if (accountOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found."));
        }

        AccountModel account = accountOpt.get();
        Map<String, Object> userData = getUserData(account);

        // ✅ Inject session playtime from cache
        if (sessionPlayTimeCache.containsKey(id)) {
            userData.put("userSessionPlayTime", sessionPlayTimeCache.get(id));
        }

        return ResponseEntity.ok(userData);
    }

    // POST: /accountUsers/{userID}/addFriend/{friendID}
    @PostMapping("/accountUsers/{userID}/addFriend/{friendID}")
    public ResponseEntity<?> addFriend(@PathVariable Long userID, @PathVariable Long friendID) {
        if(Objects.equals(userID, friendID)) {
            return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't add yourself as a friended user");
        }
        return updateUserListWithIDs(userID, friendID, true, true);
    }

    @DeleteMapping("/accountUsers/{userID}/removeFriend/{friendID}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userID, @PathVariable Long friendID) {
        return updateUserListWithIDs(userID, friendID, false, true);
    }

    @PostMapping("/accountUsers/{userID}/addBlockedUser/{blockedID}")
    public ResponseEntity<?> addBlockedUser(@PathVariable Long userID, @PathVariable Long blockedID) {
        if(Objects.equals(userID, blockedID)) {
            return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't add yourself as a blocked user");
        }
        return updateUserListWithIDs(userID, blockedID, true, false);
    }

    @DeleteMapping("/accountUsers/{userID}/removeBlockedUser/{blockedID}")
    public ResponseEntity<?> removeBlockedUser(@PathVariable Long userID, @PathVariable Long blockedID) {
        return updateUserListWithIDs(userID, blockedID, false, false);
    }

    @PutMapping("/accountUsers/{userID}/updateFriendsList")
    public ResponseEntity<?> updateUserFriendsList(@PathVariable Long userID, @RequestBody List<Long> newFriendsList) {
        Optional<AccountModel> userOptional = myAccountRepository.findById(userID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + userID + " not found.");
        }
        AccountModel user = userOptional.get();

        // ✅ Step 1: Remove all current friends using `removeFriend()`
        for (Long currentFriendID : new ArrayList<>(user.getFriendsList())) {
            removeFriend(userID, currentFriendID);
        }

        // ✅ Step 2: Attempt to add each new friend, skipping invalid ones
        List<Long> successfullyAdded = new ArrayList<>();
        List<Long> failedToAdd = new ArrayList<>();

        for (Long newFriendID : newFriendsList) {
            if (newFriendID.equals(userID)) { // ❌ Prevent self-friendship
                failedToAdd.add(newFriendID);
                continue;
            }

            ResponseEntity<?> response = addFriend(userID, newFriendID);
            if (response.getStatusCode().is2xxSuccessful()) {
                successfullyAdded.add(newFriendID);
            } else {
                failedToAdd.add(newFriendID);
            }
        }

        String resultMessage = "Friends list updated. Added: " + successfullyAdded;
        if (!failedToAdd.isEmpty()) {
            resultMessage += ". Failed to add: " + failedToAdd;
        }

        return ResponseEntity.ok(resultMessage);
    }

    /** ✅ List a specific blocked user */
    @GetMapping("/{ID}/listBlockedUser/{userID}")
    public ResponseEntity<?> listSpecificBlockedUser(@PathVariable Long ID, @PathVariable Long userID) {
        return myAccountRepository.findById(ID)
                .map(account -> account.getBlockedList().contains(userID) ? ResponseEntity.ok(userID) :
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Blocked user not found")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found")));
    }

    /** ✅ List a specific friend */
    @GetMapping("/{ID}/listFriend/{userID}")
    public ResponseEntity<?> listSpecificFriend(@PathVariable Long ID, @PathVariable Long userID) {
        return myAccountRepository.findById(ID)
                .map(account -> account.getFriendsList().contains(userID) ? ResponseEntity.ok(userID) :
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Friend not found")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found")));
    }

    @PutMapping("/accountUsers/{userID}/updateBlockedList")
    public ResponseEntity<?> updateUserBlockedList(@PathVariable Long userID, @RequestBody List<Long> newBlockedList) {
        Optional<AccountModel> userOptional = myAccountRepository.findById(userID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + userID + " not found.");
        }
        AccountModel user = userOptional.get();

        // ✅ Step 1: Remove all currently blocked users using `removeBlockedUser()`
        for (Long currentBlockedID : new ArrayList<>(user.getBlockedList())) {
            removeBlockedUser(userID, currentBlockedID);
        }

        // ✅ Step 2: Block all new users, skipping invalid ones
        List<Long> successfullyBlocked = new ArrayList<>();
        List<Long> failedToBlock = new ArrayList<>();

        for (Long newBlockedID : newBlockedList) {
            if (newBlockedID.equals(userID)) { // ❌ Prevent self-blocking
                failedToBlock.add(newBlockedID);
                continue;
            }

            Optional<AccountModel> blockedUserOptional = myAccountRepository.findById(newBlockedID);
            if (blockedUserOptional.isPresent()) {
                addBlockedUser(userID, newBlockedID);
                successfullyBlocked.add(newBlockedID);
            } else {
                failedToBlock.add(newBlockedID);
            }
        }

        String resultMessage = "Blocked list updated. Blocked: " + successfullyBlocked;
        if (!failedToBlock.isEmpty()) {
            resultMessage += ". Failed to block: " + failedToBlock;
        }

        return ResponseEntity.ok(resultMessage);
    }

    private List<AccountModel> getUsersByIds(Long userID, Long targetID) throws Exception {
        AccountModel user = myAccountRepository.findById(userID)
                .orElseThrow(() -> new Exception("User ID " + userID + " not found."));
        AccountModel target = myAccountRepository.findById(targetID)
                .orElseThrow(() -> new Exception("Target ID " + targetID + " not found."));
        return List.of(user, target);
    }

    private ResponseEntity<?> updateUserListWithIDs(Long userID, Long targetID, boolean isAdding, boolean isFriendList) {
        try {
            List<AccountModel> users = getUsersByIds(userID, targetID);
            AccountModel user = users.get(0);
            AccountModel target = users.get(1);

            List<Long> userList = isFriendList ? user.getFriendsList() : user.getBlockedList();

            // ❌ Reject friend request if the target is blocked
            if (isAdding && isFriendList) {
                if (user.getBlockedList().contains(targetID)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Cannot add user ID " + targetID + " as a friend because they are in your blocked list.");
                }
                if (target.getBlockedList().contains(userID)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Cannot add user ID " + targetID + " as a friend because they have blocked you.");
                }
            }

            if (isAdding) {
                if (!userList.contains(targetID)) userList.add(targetID);
                if (isFriendList && !target.getFriendsList().contains(userID)) {
                    target.getFriendsList().add(userID);
                }
            } else {
                userList.remove(targetID);
                if (isFriendList) {
                    target.getFriendsList().remove(userID);
                }
            }

            // ✅ If adding to `blockedList`, remove from `friendsList`
            if (isAdding && !isFriendList) {
                user.getFriendsList().remove(targetID);
                target.getFriendsList().remove(userID);
            }

            if (isFriendList) {
                user.setFriendsList(userList);
                target.setFriendsList(target.getFriendsList());
                myAccountRepository.save(target);
            } else {
                user.setBlockedList(userList);
            }

            myAccountRepository.save(user);
            return ResponseEntity.ok((isAdding ? "Added " : "Removed ") + "user ID " + targetID + " successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ GET: List Friends
    @GetMapping("/accountUsers/{ID}/listFriends")
    public ResponseEntity<?> listFriends(@PathVariable Long ID) {
        Optional<AccountModel> userOptional = myAccountRepository.findById(ID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + ID + " not found.");
        }

        AccountModel user = userOptional.get();
        return ResponseEntity.ok(user.getFriendsList()); // Returns List<Long>
    }

    // ✅ GET: List Blocked Users
    @GetMapping("/accountUsers/{ID}/listBlockedUsers")
    public ResponseEntity<?> listBlockedUsers(@PathVariable Long ID) {
        Optional<AccountModel> userOptional = myAccountRepository.findById(ID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + ID + " not found.");
        }

        AccountModel user = userOptional.get();
        return ResponseEntity.ok(user.getBlockedList()); // Returns List<Long>
    }

    // ✅ GET: List all users who are online
    @GetMapping("/accountUsers/listOnline")
    public ResponseEntity<List<AccountModel>> listOnlineUsers() {
        List<AccountModel> onlineUsers = myAccountRepository.findAll().stream()
                .filter(user -> Optional.ofNullable(user.getIsOnline()).orElse(false)) // ✅ Prevents null issues
                .toList();
        return ResponseEntity.ok(onlineUsers);
    }

    // ✅ GET: List all users who are currently playing
    @GetMapping("/accountUsers/listPlaying")
    public ResponseEntity<List<AccountModel>> listPlayingUsers() {
        List<AccountModel> playingUsers = myAccountRepository.findAll().stream()
                .filter(user -> Optional.ofNullable(user.getIsPlaying()).orElse(false)) // ✅ Prevents null issues
                .toList();
        return ResponseEntity.ok(playingUsers);
    }

    // ✅ GET: List all users who are in queue
    @GetMapping("/accountUsers/listInQueue")
    public ResponseEntity<List<AccountModel>> listUsersInQueue() {
        List<AccountModel> queueUsers = myAccountRepository.findAll().stream()
                .filter(user -> Optional.ofNullable(user.getIsInQueue()).orElse(false)) // ✅ Prevents null issues
                .toList();
        return ResponseEntity.ok(queueUsers);
    }

    // ✅ POST: Upload Profile Picture
    @PostMapping("/accountUsers/{userID}/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long userID, @RequestParam("file") MultipartFile file) {
        try {
            Optional<AccountModel> userOptional = myAccountRepository.findById(userID);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            AccountModel user = userOptional.get();

            // Ensure profile picture directory exists
            File directory = new File(PROFILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // ✅ STEP 1: DELETE ALL PREVIOUS PROFILE PICTURES (any extension)
            File[] existingFiles = directory.listFiles((dir, name) -> name.startsWith(userID + "."));
            if (existingFiles != null) {
                for (File oldFile : existingFiles) {
                    oldFile.delete(); // Delete each existing profile picture
                }
            }

            // ✅ STEP 2: SAVE NEW PROFILE PICTURE
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = userID + fileExtension; // Store as "<userID>.png" or "<userID>.jpeg"
            Path filePath = Paths.get(PROFILE_DIRECTORY, filename);

            Files.write(filePath, file.getBytes());

            // ✅ STEP 3: UPDATE USER PROFILE PICTURE PATH
            user.setProfilePicturePath(filename);
            myAccountRepository.save(user);

            return ResponseEntity.ok("Profile picture uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    // ✅ GET: Retrieve Profile Picture
    @GetMapping("/accountUsers/{userID}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userID) {
        try {
            Optional<AccountModel> userOptional = myAccountRepository.findById(userID);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            AccountModel user = userOptional.get();
            String profilePicturePath = user.getProfilePicturePath();

            // If user has no profile picture, serve default.png
            if (profilePicturePath == null || profilePicturePath.equals("default.png")) {
                profilePicturePath = PROFILE_DIRECTORY + "default.png";
            } else {
                profilePicturePath = PROFILE_DIRECTORY + profilePicturePath;
            }

            File imgFile = new File(profilePicturePath);
            if (!imgFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(imgFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ DELETE: Remove user profile picture (resets to default.png)
    @DeleteMapping("/accountUsers/{userID}/deleteProfilePicture")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable Long userID) {
        try {
            Optional<AccountModel> userOptional = myAccountRepository.findById(userID);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            AccountModel user = userOptional.get();
            String profilePicturePath = user.getProfilePicturePath();

            if (profilePicturePath != null && !profilePicturePath.equals("default.png")) {
                File existingFile = new File(PROFILE_DIRECTORY + profilePicturePath);
                if (existingFile.exists()) {
                    existingFile.delete(); // ✅ Delete old profile picture file
                }
            }

            // ✅ Reset user profile picture to default
            user.setProfilePicturePath("default.png");
            myAccountRepository.save(user);

            return ResponseEntity.ok("Profile picture deleted and reset to default.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting profile picture: " + e.getMessage());
        }
    }

    @PutMapping("/accountUsers/{userID}/addPlayTime")
    public ResponseEntity<?> addPlayTime(@PathVariable Long userID, @RequestBody AccountPlayTime playTimeToAdd) {
        Optional<AccountModel> accountOptional = myAccountRepository.findById(userID);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        }

        AccountModel account = accountOptional.get();

        // ✅ Prevent updating if user is offline
        if (!Boolean.TRUE.equals(account.getIsOnline())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Cannot add playtime while offline."));
        }

        // ✅ Retrieve or initialize the session playtime from cache
        AccountPlayTime sessionPlayTime = sessionPlayTimeCache.getOrDefault(userID, new AccountPlayTime());

        // ✅ Add playtime to totalUserPlayTime (Stored in DB)
        account.getTotalUserPlayTime().addTime(playTimeToAdd.getDays(), playTimeToAdd.getHours(), playTimeToAdd.getMinutes(), playTimeToAdd.getSeconds());

        // ✅ Add playtime to sessionPlayTime (Runtime Only)
        sessionPlayTime.addTime(playTimeToAdd.getDays(), playTimeToAdd.getHours(), playTimeToAdd.getMinutes(), playTimeToAdd.getSeconds());

        // ✅ Update cache with new session playtime
        sessionPlayTimeCache.put(userID, sessionPlayTime);

        // ✅ Save only totalUserPlayTime to MySQL (NOT session playtime)
        myAccountRepository.save(account);

        return ResponseEntity.ok(Map.of(
                "message", "Playtime updated successfully.",
                "totalPlayTime", account.getTotalUserPlayTime().toString(),
                "sessionPlayTime", sessionPlayTime.toString()
        ));
    }
}
