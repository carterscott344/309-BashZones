package ZoneZone.com.itemsHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/userItems")
public class UserItemController {

    private final UserItemRepository userItemRepository;

    public UserItemController(UserItemRepository userItemRepository) {
        this.userItemRepository = userItemRepository;
    }

    // ✅ Add an item for a user
    @PostMapping("/{userID}/addItem/{serverItemID}")
    public ResponseEntity<UserItemModel> addItem(@PathVariable Long userID, @PathVariable Long serverItemID,
                                                 @RequestBody(required = false) UserItemModel newItem) {
        if (newItem == null) {
            newItem = new UserItemModel(serverItemID, userID, LocalDate.now().toString(), false);
        } else {
            newItem = new UserItemModel(serverItemID, userID, LocalDate.now().toString(), newItem.getIsEquipped());
        }
        UserItemModel savedItem = userItemRepository.save(newItem);
        return ResponseEntity.ok(savedItem);
    }

    // ✅ Remove an item for a user
    @DeleteMapping("/{userID}/removeItem/{itemID}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> item = userItemRepository.findById(itemID);
        if (item.isPresent() && item.get().getBelongsToAccountID().equals(userID)) {
            userItemRepository.deleteById(itemID);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{userID}/editItem/{itemID}")
    public ResponseEntity<UserItemModel> editItem(@PathVariable Long userID, @PathVariable Long itemID,
                                                  @RequestBody UserItemModel updatedItem) {
        Optional<UserItemModel> optionalItem = userItemRepository.findById(itemID);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserItemModel existingItem = optionalItem.get();
        if (!userID.equals(existingItem.getBelongsToAccountID())) {
            return ResponseEntity.badRequest().build();
        }

        // Only update fields that are present in the request
        if (updatedItem.getIsEquipped() != null) {
            existingItem.setIsEquipped(updatedItem.getIsEquipped());
        }
        if (updatedItem.getServerItemTypeID() != null) {
            existingItem.setServerItemTypeID(updatedItem.getServerItemTypeID());
        }

        // Save the updated item
        UserItemModel savedItem = userItemRepository.save(existingItem);
        return ResponseEntity.ok(savedItem);
    }


    // ✅ Get all items belonging to a user
    @GetMapping("/{userID}/listItems")
    public ResponseEntity<List<UserItemModel>> listItems(@PathVariable Long userID) {
        return ResponseEntity.ok(userItemRepository.findByBelongsToAccountID(userID));
    }

    // ✅ Get a specific item for a user
    @GetMapping("/{userID}/listItem/{itemID}")
    public ResponseEntity<UserItemModel> getItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> optionalItem = userItemRepository.findById(itemID);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserItemModel item = optionalItem.get();
        if (!userID.equals(item.getBelongsToAccountID())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(item);
    }


    // ✅ Get all user items globally
    @GetMapping("/listItems")
    public ResponseEntity<List<UserItemModel>> listAllItems() {
        return ResponseEntity.ok(userItemRepository.findAll());
    }

    // ✅ Get a specific item globally
    @GetMapping("/listItem/{itemID}")
    public ResponseEntity<UserItemModel> getItemGlobal(@PathVariable Long itemID) {
        return userItemRepository.findById(itemID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
