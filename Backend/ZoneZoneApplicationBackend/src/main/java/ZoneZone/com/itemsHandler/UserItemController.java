package ZoneZone.com.itemsHandler;

import ZoneZone.com.accountHandler.AccountModel;
import ZoneZone.com.accountHandler.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/userItems")
public class UserItemController {

    private static final Logger logger = LoggerFactory.getLogger(UserItemController.class);

    private final UserItemRepository userItemRepository;
    private final ServerItemRepository serverItemRepository;
    private final AccountRepository accountRepository;

    public UserItemController(UserItemRepository userItemRepository, ServerItemRepository serverItemRepository, AccountRepository accountRepository) {
        this.userItemRepository = userItemRepository;
        this.serverItemRepository = serverItemRepository;
        this.accountRepository = accountRepository;
    }

    /** ✅ Add a new item to a user (Prevent duplicate purchases) */
    @Transactional
    @PostMapping("/{userID}/addItem/{serverItemID}")
    public ResponseEntity<?> addItemToUser(@PathVariable Long userID, @PathVariable Long serverItemID) {
        try {
            Optional<AccountModel> userOpt = accountRepository.findById(userID);
            Optional<ServerItemModel> serverItemOpt = serverItemRepository.findById(serverItemID);

            if (userOpt.isEmpty()) {
                logger.warn("Add item failed: User {} not found.", userID);
                return ResponseEntity.badRequest().body(Map.of("error", "User not found."));
            }

            if (serverItemOpt.isEmpty()) {
                logger.warn("Add item failed: Server item {} not found.", serverItemID);
                return ResponseEntity.badRequest().body(Map.of("error", "Server item not found."));
            }


            AccountModel user = userOpt.get();
            ServerItemModel serverItem = serverItemOpt.get();

            if (userItemRepository.existsByBelongToAccountAndServerItem(user, serverItem)) {
                return ResponseEntity.badRequest().body(Map.of("error", "You already own this item."));
            }

            UserItemModel newItem = new UserItemModel();
            newItem.setBelongToAccount(user);
            newItem.setEquipped(false);
            newItem.setServerItem(serverItem);
            newItem.setDatePurchased(new Date());

            userItemRepository.save(newItem);
            return ResponseEntity.ok(Map.of("message", "Item added successfully", "itemId", newItem.getItemID()));

        } catch (Exception e) {
            logger.error("Unexpected error in {}()", Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /** ✅ Remove an item from a user */
    @Transactional
    @DeleteMapping("/{userID}/removeItem/{itemID}")
    public ResponseEntity<?> removeUserItem(@PathVariable Long userID, @PathVariable Long itemID) {
        try {
            Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

            if (itemOpt.isEmpty()) {
                logger.warn("Attempted to remove non-existent item. itemId: {}", itemID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item ID not found."));
            }

            UserItemModel item = itemOpt.get();
            if (!item.getBelongToAccount().getAccountID().equals(userID)) {
                logger.warn("User {} attempted to remove item {} that does not belong to them.", userID, itemID);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Item does not belong to user."));
            }

            userItemRepository.deleteById(itemID);
            logger.info("User {} successfully removed item {}", userID, itemID);
            return ResponseEntity.ok(Map.of("message", "Item removed successfully", "itemId", itemID));

        } catch (Exception e) {
            logger.error("Unexpected error in removeUserItem()", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /** ✅ Edit an existing user item (Ensure only one item of each type is equipped) */
    @Transactional
    @PutMapping("/{userID}/editItem/{itemID}")
    public ResponseEntity<?> editUserItem(@PathVariable Long userID, @PathVariable Long itemID, @RequestBody Map<String, Object> requestBody) {
        try {
            Optional<UserItemModel> itemOpt = userItemRepository.findById(itemID);

            logger.debug("Edit request received for itemID {} by userID {}", itemID, userID);

            if (itemOpt.isEmpty()) {
                logger.warn("ItemID {} does not exist in the database.", itemID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item ID not found."));
            }

            UserItemModel item = itemOpt.get();
            if (!item.getBelongToAccount().getAccountID().equals(userID)) {
                logger.warn("User {} attempted to edit item {} that does not belong to them.", userID, itemID);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Item does not belong to user."));
            }

            boolean updated = false;

            // ✅ Extract values only once (more efficient)
            boolean newEquipState = (Boolean) requestBody.getOrDefault("isEquipped", item.getIsEquipped());
            Date newDatePurchased = new Date((Long) requestBody.getOrDefault("datePurchased", item.getDatePurchased().getTime()));

            // ✅ Handle equipping logic (ensure only one item of each type is equipped)
            if (newEquipState != item.getIsEquipped()) {
                if (newEquipState) {
                    List<UserItemModel> equippedItems = userItemRepository.findByBelongToAccountAndIsEquipped(item.getBelongToAccount(), true);

                    for (UserItemModel equippedItem : equippedItems) {
                        if (!equippedItem.getItemID().equals(itemID) &&
                                equippedItem.getServerItem().getServerItemType().equals(item.getServerItem().getServerItemType())) {
                            equippedItem.setEquipped(false);
                            userItemRepository.save(equippedItem);
                            logger.info("User {} unequipped item {} of type {}", userID, equippedItem.getItemID(), equippedItem.getServerItem().getServerItemType());
                        }
                    }
                }

                item.setEquipped(newEquipState);
                updated = true;
            }

            // ✅ Only update purchase date if it's changed
            if (!newDatePurchased.equals(item.getDatePurchased())) {
                item.setDatePurchased(newDatePurchased);
                updated = true;
            }

            if (!updated) {
                logger.info("User {} attempted to edit item {}, but no changes were made.", userID, itemID);
                return ResponseEntity.ok(Map.of("message", "No changes detected. Item remains the same."));
            }

            userItemRepository.save(item);
            logger.info("User {} successfully edited item {} (Equipped: {}, Date Purchased: {})",
                    userID, itemID, newEquipState, newDatePurchased);
            return ResponseEntity.ok(Map.of("message", "Item updated successfully", "itemId", itemID));

        } catch (Exception e) {
            logger.error("Unexpected error in editUserItem()", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }


    /** ✅ Get a user's item inventory */
    @GetMapping("/{userID}/itemInventory")
    public ResponseEntity<?> getUserInventory(@PathVariable Long userID) {
        Optional<AccountModel> userOpt = accountRepository.findById(userID);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        }

        List<Map<String, Object>> formattedItems = userItemRepository.findByBelongToAccount(userOpt.get())
                .stream()
                .map(item -> Map.of(
                        "itemId", item.getItemID(),
                        "datePurchased", item.getDatePurchased(),
                        "isEquipped", item.getIsEquipped(),
                        "serverItem", formatServerItem(item.getServerItem())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(formattedItems);
    }

    /** ✅ Get all equipped items for a user */
    @GetMapping("/{userID}/listEquipped")
    public ResponseEntity<?> listEquippedItems(@PathVariable Long userID) {
        return getUserInventory(userID);
    }

    /** ✅ List all user items */
    @GetMapping("/listItems")
    public ResponseEntity<List<Map<String, Object>>> listUserItems() {
        return ResponseEntity.ok(userItemRepository.findAll()
                .stream()
                .map(item -> Map.of(
                        "itemId", item.getItemID(),
                        "belongsToAccountID", item.getBelongToAccountID(), // ✅ Explicitly include
                        "datePurchased", item.getDatePurchased(),
                        "isEquipped", item.getIsEquipped(),
                        "serverItem", formatServerItem(item.getServerItem())
                ))
                .collect(Collectors.toList()));
    }

    /** ✅ List all user items with pagination */
    @GetMapping("/listItemsPaged")
    public ResponseEntity<Page<Map<String, Object>>> listItemsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserItemModel> pagedItems = userItemRepository.findAll(PageRequest.of(page, size));

        Page<Map<String, Object>> formattedItems = pagedItems.map(item -> Map.of(
                "itemId", item.getItemID(),
                "belongsToAccountID", item.getBelongToAccountID(), // ✅ Explicitly include
                "datePurchased", item.getDatePurchased(),
                "isEquipped", item.getIsEquipped(),
                "serverItem", formatServerItem(item.getServerItem())
        ));

        return ResponseEntity.ok(formattedItems);
    }

    private Map<String, Object> formatServerItem(ServerItemModel serverItem) {
        return Map.of(
                "serverItemId", serverItem.getServerItemID(),
                "serverItemName", serverItem.getServerItemName(),
                "serverItemDescription", serverItem.getServerItemDescription(),
                "serverItemType", serverItem.getServerItemType(),
                "itemCost", serverItem.getItemCost()
        );
    }

    private Optional<UserItemModel> validateItemOwnership(Long userID, Long itemID) {
        return userItemRepository.findById(itemID)
                .filter(item -> item.getBelongToAccount().getAccountID().equals(userID));
    }
}
