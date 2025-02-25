package ZoneZone.com.accountHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AccountModel> createUser(@RequestBody AccountModel account) {
        AccountModel savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // DELETE: /accountUsers/deleteUser/{userID}
    @DeleteMapping("/accountUsers/deleteUser/{userID}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userID) {
        if (accountRepository.existsById(userID)) {
            accountRepository.deleteById(userID);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT: /accountUsers/updateUser/{userID}
    @PutMapping("/accountUsers/updateUser/{userID}")
    public ResponseEntity<AccountModel> updateUser(@PathVariable Long userID, @RequestBody AccountModel updatedAccount) {
        Optional<AccountModel> accountOptional = accountRepository.findById(userID);
        if (accountOptional.isPresent()) {
            AccountModel account = accountOptional.get();

            // Update fields (adjust as needed)
            account.setAccountUsername(updatedAccount.getAccountUsername());
            account.setAccountPassword(updatedAccount.getAccountPassword());
            account.setFirstName(updatedAccount.getFirstName());
            account.setLastName(updatedAccount.getLastName());
            account.setAccountEmail(updatedAccount.getAccountEmail());
            account.setUserBirthday(updatedAccount.getUserBirthday());
            account.setIsAdmin(updatedAccount.getIsAdmin());
            account.setIsBlocked(updatedAccount.getIsBlocked());
            account.setUserLevel(updatedAccount.getUserLevel());
            account.setCurrentLevelXP(updatedAccount.getCurrentLevelXP());
            account.setUserAge(updatedAccount.getUserAge());
            account.setGemBalance(updatedAccount.getGemBalance());
            account.setFriendsList(updatedAccount.getFriendsList());
            account.setBlockedList(updatedAccount.getBlockedList());
            account.setItemsList(updatedAccount.getItemsList());

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
}
