package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import ZoneZone.com.accountHandler.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/userItems")
public class UserItemController {

    private final UserItemRepository userItemRepository;
    private final ServerItemRepository serverItemRepository;
    private final AccountRepository accountRepository;

    public UserItemController(UserItemRepository userItemRepository, ServerItemRepository serverItemRepository, AccountRepository accountRepository) {
        this.userItemRepository = userItemRepository;
        this.serverItemRepository = serverItemRepository;
        this.accountRepository = accountRepository;
    }

    @PutMapping("/{userID}/editItem/{itemID}")
    public ResponseEntity<UserItemModel> editUserItem(@PathVariable Long userID, @PathVariable Long itemID, @RequestBody Map<String, Boolean> requestBody) {
        Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

        if (itemOpt.isEmpty() || !itemOpt.get().getBelongToAccount().equals(userID)) {
            return ResponseEntity.badRequest().build();
        }

        UserItemModel item = itemOpt.get();
        boolean newIsEquipped = requestBody.getOrDefault("isEquipped", item.isEquipped());
        item.setEquipped(newIsEquipped);
        userItemRepository.save(item);

        return ResponseEntity.ok(item);
    }

    // ✅ 2. Remove an item from a user
    @DeleteMapping("/{userID}/removeItem/{itemID}")
    public ResponseEntity<?> removeUserItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

        if (itemOpt.isEmpty() || !itemOpt.get().getBelongToAccount().equals(userID)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Item not found or does not belong to user.\"}"); // ✅ Return JSON error message
        }

        userItemRepository.deleteById(itemID);
        return ResponseEntity.ok().body("{\"message\": \"Item removed successfully\"}"); // ✅ Return JSON success message
    }


    // ✅ 3. Get details of a specific item
    @GetMapping("/{userID}/listItem/{itemID}")
    public ResponseEntity<?> getUserItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

        if (itemOpt.isEmpty() || !itemOpt.get().getBelongToAccount().equals(userID)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item not found or does not belong to user."));
        }

        return ResponseEntity.ok(itemOpt.get());
    }

    // ✅ Existing methods remain unchanged
    @PostMapping("/{userID}/addItem/{serverItemID}")
    public ResponseEntity<String> addItemToUser(@PathVariable Long userID, @PathVariable Long serverItemID) {
        Optional<AccountModel> user = accountRepository.findById(userID);
        Optional<ServerItemModel> serverItem = serverItemRepository.findById(serverItemID);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        if (serverItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Server item not found.");
        }

        AccountModel userAccount = user.get();
        UserItemModel newItem = new UserItemModel(serverItem.get(), new Date(), false, userID);
        userItemRepository.save(newItem);

        userAccount.getOwnedPlayerItems().add(newItem.getId());
        accountRepository.save(userAccount);

        return ResponseEntity.ok("Item added successfully to user.");
    }

    @GetMapping("/listItems")
    public ResponseEntity<List<UserItemModel>> listUserItems() {
        List<UserItemModel> userItems = userItemRepository.findAll();
        return ResponseEntity.ok(userItems);
    }

    @GetMapping("/{userID}/itemInventory")
    public ResponseEntity<List<UserItemModel>> getUserInventory(@PathVariable Long userID) {
        List<UserItemModel> items = userItemRepository.findByBelongToAccountID(userID);
        return ResponseEntity.ok(items);
    }
}
