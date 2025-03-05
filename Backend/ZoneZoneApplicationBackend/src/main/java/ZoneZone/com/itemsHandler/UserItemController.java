package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import ZoneZone.com.accountHandler.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    // ✅ 1. Add an item to a user based on an existing ServerItem
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

        // Create a new UserItemModel instance
        UserItemModel newItem = new UserItemModel(serverItem.get(), new Date(), false, user.get());
        userItemRepository.save(newItem);

        return ResponseEntity.ok("Item added successfully to user.");
    }


    // ✅ 2. List all user items
    @GetMapping("/listItems")
    public ResponseEntity<List<UserItemModel>> listUserItems() {
        List<UserItemModel> userItems = userItemRepository.findAll();
        return ResponseEntity.ok(userItems);
    }
}
