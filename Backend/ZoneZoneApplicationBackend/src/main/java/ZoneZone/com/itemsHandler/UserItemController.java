package ZoneZone.com.itemsHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/userItems")
public class UserItemController {

    private final UserItemRepository userItemRepository;

    public UserItemController(UserItemRepository userItemRepository) {
        this.userItemRepository = userItemRepository;
    }

    // 1. ADD ITEM: POST /userItems/{userID}/addItem/{serverItemID}
    @PostMapping("/{userID}/addItem/{serverItemID}")
    public ResponseEntity<UserItemModel> addItem(
            @PathVariable Long userID,
            @PathVariable Long serverItemID,
            @RequestBody(required = false) UserItemModel newItem) {

        // Set default values if body is missing
        if (newItem == null) {
            newItem = new UserItemModel(userID, "Default Item", "Default Type");
        }

        // Ensure correct user assignment
        newItem.setAccountOwnerID(userID);
        UserItemModel savedItem = userItemRepository.save(newItem);

        return ResponseEntity.ok(savedItem);
    }

    // 2. REMOVE ITEM: DELETE /userItems/{userID}/removeItem/{itemID}
    @DeleteMapping("/{userID}/removeItem/{itemID}")
    public ResponseEntity<String> removeItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> item = userItemRepository.findById(itemID);

        if (item.isPresent() && item.get().getAccountOwnerID().equals(userID)) {
            userItemRepository.deleteById(itemID);
            return ResponseEntity.ok("Item removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Item not found or does not belong to user.");
        }
    }

    // 3. EDIT ITEM: PUT /userItems/{userID}/editItem/{itemID}
    @PutMapping("/{userID}/editItem/{itemID}")
    public ResponseEntity<?> editItem(@PathVariable Long userID,
                                      @PathVariable Long itemID,
                                      @RequestBody UserItemModel updatedItem) {
        Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

        if (itemOpt.isPresent()) {
            UserItemModel existingItem = itemOpt.get();

            // Ensure the item belongs to the user
            if (!existingItem.getAccountOwnerID().equals(userID)) {
                return ResponseEntity.status(403).body("You are not the owner of this item.");
            }

            // Update only non-null fields
            if (updatedItem.getItemName() != null) {
                existingItem.setItemName(updatedItem.getItemName());
            }
            if (updatedItem.getItemType() != null) {
                existingItem.setItemType(updatedItem.getItemType());
            }

            UserItemModel savedItem = userItemRepository.save(existingItem);
            return ResponseEntity.ok(savedItem);
        } else {
            return ResponseEntity.status(404).body("Item not found.");
        }
    }


    // 4. LIST ALL ITEMS: GET /userItems/{userID}/listItems
    @GetMapping("/{userID}/listItems")
    public ResponseEntity<List<UserItemModel>> listItems(@PathVariable Long userID) {
        List<UserItemModel> items = userItemRepository.findByAccountOwnerID(userID);

        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(items);
    }

    // 5. GET SINGLE ITEM: GET /userItems/{userID}/listItem/{itemID}
    @GetMapping("/{userID}/listItem/{itemID}")
    public ResponseEntity<?> getItem(@PathVariable Long userID, @PathVariable Long itemID) {
        Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

        if (itemOpt.isPresent()) {
            UserItemModel item = itemOpt.get();

            // Ensure the item belongs to the user
            if (!item.getAccountOwnerID().equals(userID)) {
                return ResponseEntity.status(403).body("Item does not belong to this user.");
            }

            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.status(404).body("Item not found.");
        }
    }
    }
