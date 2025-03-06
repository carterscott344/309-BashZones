package ZoneZone.com.accountHandler;

import ZoneZone.com.itemsHandler.UserItemModel;
import ZoneZone.com.itemsHandler.UserItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
public class AccountController {

    private final AccountRepository myAccountRepository;
    private final UserItemRepository myUserItemRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository, UserItemRepository userItemRepository) {
        this.myAccountRepository = accountRepository;
        this.myUserItemRepository = userItemRepository;
    }

    // POST: /accountUsers/createUser
    @PostMapping("/accountUsers/createUser")
    public ResponseEntity<?> createUser(@RequestBody AccountModel account) {
        try {
            AccountModel savedAccount = myAccountRepository.save(account);
            return ResponseEntity.ok(savedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    // DELETE: /accountUsers/deleteUser/{userID}
    @DeleteMapping("/accountUsers/deleteUser/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userID) {
        if (myAccountRepository.existsById(userID)) {
            myAccountRepository.deleteById(userID);
            return ResponseEntity.ok().body("{\"message\": \"User deleted successfully\"}"); // ✅ Return JSON message
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\": \"User not found\"}"); // ✅ Handle 404 properly
    }


    @PutMapping("/accountUsers/updateUser/{userID}")
    public ResponseEntity<?> updateUser(@PathVariable Long userID, @RequestBody AccountModel updatedAccount) {
        Optional<AccountModel> accountOptional = myAccountRepository.findById(userID);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + userID + " not found.");
        }

        AccountModel account = accountOptional.get();

        try {
            // ✅ Prevent Null Pointer Issues by Checking for Non-Null Updates
            if (updatedAccount.getAccountUsername() != null) {
                account.setAccountUsername(updatedAccount.getAccountUsername());
            }
            if (updatedAccount.getAccountPassword() != null) {
                account.setAccountPassword(updatedAccount.getAccountPassword());
            }
            if (updatedAccount.getAccountType() != null) {
                account.setAccountType(updatedAccount.getAccountType());
            }
            if (updatedAccount.getFirstName() != null) {
                account.setFirstName(updatedAccount.getFirstName());
            }
            if (updatedAccount.getLastName() != null) {
                account.setLastName(updatedAccount.getLastName());
            }
            if (updatedAccount.getAccountEmail() != null) {
                account.setAccountEmail(updatedAccount.getAccountEmail());
            }
            if (updatedAccount.getUserBirthday() != null) {
                account.setUserBirthday(updatedAccount.getUserBirthday());
            }
            if (updatedAccount.getIsBanned() != null) {
                account.setIsBanned(updatedAccount.getIsBanned());
            }
            if (updatedAccount.getUserLevel() != 0) {
                account.setUserLevel(updatedAccount.getUserLevel());
            }
            if (updatedAccount.getCurrentLevelXP() != 0) {
                account.setCurrentLevelXP(updatedAccount.getCurrentLevelXP());
            }
            if (updatedAccount.getGemBalance() != 0) {
                account.setGemBalance(updatedAccount.getGemBalance());
            }
            myAccountRepository.save(account);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    private Map<String, Object> getUserData(AccountModel account) {
        Map<String, Object> userData = new HashMap<>();

        // ✅ Include all account details
        userData.put("accountID", account.getAccountID());
        userData.put("accountType", account.getAccountType());
        userData.put("isBanned", account.getIsBanned());
        userData.put("accountUsername", account.getAccountUsername());
        userData.put("accountPassword", account.getAccountPassword());
        userData.put("firstName", account.getFirstName());
        userData.put("lastName", account.getLastName());
        userData.put("accountEmail", account.getAccountEmail());
        userData.put("userBirthday", account.getUserBirthday());
        userData.put("userAge", account.getUserAge());
        userData.put("userLevel", account.getUserLevel());
        userData.put("currentLevelXP", account.getCurrentLevelXP());
        userData.put("gemBalance", account.getGemBalance());
        userData.put("friendsList", account.getFriendsList());
        userData.put("blockedList", account.getBlockedList());

        // ✅ Fetch full item details from stored item IDs
        List<UserItemModel> userItems = myUserItemRepository.findAllById(account.getOwnedPlayerItems());
        userData.put("ownedPlayerItems", userItems);

        return userData;
    }

    // GET: /accountUsers/listUsers - List all accounts
    @GetMapping("/accountUsers/listUsers")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        List<AccountModel> accounts = myAccountRepository.findAll();
        List<Map<String, Object>> userList = new ArrayList<>();

        for (AccountModel account : accounts) {
            Map<String, Object> userData = new HashMap<>();

            // ✅ Include all account details
            userData.put("accountID", account.getAccountID());
            userData.put("accountType", account.getAccountType());
            userData.put("isBanned", account.getIsBanned());
            userData.put("accountUsername", account.getAccountUsername());
            userData.put("accountPassword", account.getAccountPassword());
            userData.put("firstName", account.getFirstName());
            userData.put("lastName", account.getLastName());
            userData.put("accountEmail", account.getAccountEmail());
            userData.put("userBirthday", account.getUserBirthday());
            userData.put("userAge", account.getUserAge());
            userData.put("userLevel", account.getUserLevel());
            userData.put("currentLevelXP", account.getCurrentLevelXP());
            userData.put("gemBalance", account.getGemBalance());
            userData.put("friendsList", account.getFriendsList());
            userData.put("blockedList", account.getBlockedList());

            // ✅ Fetch full item details from stored item IDs
            List<UserItemModel> userItems = myUserItemRepository.findAllById(account.getOwnedPlayerItems());
            userData.put("ownedPlayerItems", userItems);

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
        return ResponseEntity.ok(getUserData(account));
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
}
