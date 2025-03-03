package ZoneZone.com.accountHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
        if (accountOptional.isPresent()) {
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
            if (updatedAccount.getFriendsList() != null) {
                account.setFriendsList(updatedAccount.getFriendsList());
            }
            if (updatedAccount.getBlockedList() != null) {
                account.setBlockedList(updatedAccount.getBlockedList());
            }
            if (updatedAccount.getItemsList() != null) {
                account.setItemsList(updatedAccount.getItemsList());
            }

            AccountModel savedAccount = accountRepository.save(account);
            return ResponseEntity.ok(savedAccount);
        }
        return ResponseEntity.notFound().build();
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
        return updateUserListWithIDs(userID, friendID, true, true);
    }

    @DeleteMapping("/accountUsers/{userID}/removeFriend/{friendID}")
    public ResponseEntity<?> removeFriend(@PathVariable Long userID, @PathVariable Long friendID) {
        return updateUserListWithIDs(userID, friendID, false, true);
    }


    @PostMapping("/accountUsers/{userID}/addBlockedUser/{blockedID}")
    public ResponseEntity<?> addBlockedUser(@PathVariable Long userID, @PathVariable Long blockedID) {
        return updateUserListWithIDs(userID, blockedID, true, false);
    }

    @DeleteMapping("/accountUsers/{userID}/removeBlockedUser/{blockedID}")
    public ResponseEntity<?> removeBlockedUser(@PathVariable Long userID, @PathVariable Long blockedID) {
        return updateUserListWithIDs(userID, blockedID, false, false);
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

            // ❌ Reject friend request if either user has the other blocked
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

}
