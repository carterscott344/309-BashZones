package ZoneZone.com.accountHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class AccountController {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // POST: /accountUsers/createUser
    @PostMapping("/accountUsers/createUser")
    public ResponseEntity<?> createUser(@RequestBody AccountModel account) {
        try {
            AccountModel savedAccount = accountRepository.save(account);
            return ResponseEntity.ok(savedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    // DELETE: /accountUsers/deleteUser/{userID}
    @DeleteMapping("/accountUsers/deleteUser/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userID) {
        if (accountRepository.existsById(userID)) {
            accountRepository.deleteById(userID);
            return ResponseEntity.ok().body("{\"message\": \"User deleted successfully\"}"); // ✅ Return JSON message
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\": \"User not found\"}"); // ✅ Handle 404 properly
    }


    // PUT: /accountUsers/updateUser/{userID}
    @PutMapping("/accountUsers/updateUser/{userID}")
    public ResponseEntity<AccountModel> updateUser(@PathVariable Long userID, @RequestBody AccountModel updatedAccount) {
        Optional<AccountModel> accountOptional = accountRepository.findById(userID);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AccountModel account = accountOptional.get();

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
            account.setUserBirthday(updatedAccount.getUserBirthday()); // ✅ Only updates if provided
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
        if (updatedAccount.getFriendsList() != null) {
            account.setFriendsList(updatedAccount.getFriendsList());
        }
        if (updatedAccount.getBlockedList() != null) {
            account.setBlockedList(updatedAccount.getBlockedList());
        }
        if (updatedAccount.getItemsList() != null) {
            account.setItemsList(updatedAccount.getItemsList());
        }

        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    // GET: /accountUsers/listUsers - List all accounts
    @GetMapping("/accountUsers/listUsers")
    public ResponseEntity<List<AccountModel>> listUsers() {
        List<AccountModel> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    // GET: /accountUsers/listUser/{userID} - Get one account by ID
    @GetMapping("/accountUsers/listUser/{userID}")
    public ResponseEntity<AccountModel> listUser(@PathVariable Long userID) {
        Optional<AccountModel> accountOptional = accountRepository.findById(userID);
        return accountOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        Optional<AccountModel> userOptional = accountRepository.findById(userID);
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
        Optional<AccountModel> userOptional = accountRepository.findById(userID);
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

            Optional<AccountModel> blockedUserOptional = accountRepository.findById(newBlockedID);
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
        AccountModel user = accountRepository.findById(userID)
                .orElseThrow(() -> new Exception("User ID " + userID + " not found."));
        AccountModel target = accountRepository.findById(targetID)
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
                accountRepository.save(target);
            } else {
                user.setBlockedList(userList);
            }

            accountRepository.save(user);
            return ResponseEntity.ok((isAdding ? "Added " : "Removed ") + "user ID " + targetID + " successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ GET: List Friends
    @GetMapping("/accountUsers/{ID}/listFriends")
    public ResponseEntity<?> listFriends(@PathVariable Long ID) {
        Optional<AccountModel> userOptional = accountRepository.findById(ID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + ID + " not found.");
        }

        AccountModel user = userOptional.get();
        return ResponseEntity.ok(user.getFriendsList()); // Returns List<Long>
    }

    // ✅ GET: List Blocked Users
    @GetMapping("/accountUsers/{ID}/listBlockedUsers")
    public ResponseEntity<?> listBlockedUsers(@PathVariable Long ID) {
        Optional<AccountModel> userOptional = accountRepository.findById(ID);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID " + ID + " not found.");
        }

        AccountModel user = userOptional.get();
        return ResponseEntity.ok(user.getBlockedList()); // Returns List<Long>
    }
}
