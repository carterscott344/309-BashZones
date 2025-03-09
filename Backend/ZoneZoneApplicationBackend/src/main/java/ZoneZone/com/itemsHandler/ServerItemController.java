package ZoneZone.com.itemsHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/serverItems")
public class ServerItemController {

    private static final Logger logger = LoggerFactory.getLogger(ServerItemController.class);
    private final ServerItemRepository serverItemRepository;

    public ServerItemController(ServerItemRepository serverItemRepository) {
        this.serverItemRepository = serverItemRepository;
    }

    /** ✅ CREATE a new Server Item */
    @PostMapping("/createItem")
    public ResponseEntity<?> createServerItem(@RequestBody ServerItemModel newItem) {
        logger.info("Create request received for ServerItem: {}", newItem.getServerItemName());

        if (serverItemRepository.existsByServerItemName(newItem.getServerItemName())) {
            logger.warn("Creation failed: ServerItem '{}' already exists.", newItem.getServerItemName());
            return ResponseEntity.badRequest().body(Map.of("error", "Server item already exists"));
        }

        ServerItemModel savedItem = serverItemRepository.save(newItem);
        logger.info("ServerItem '{}' created successfully.", savedItem.getServerItemName());
        return ResponseEntity.ok(savedItem);
    }

    /** ✅ DELETE a Server Item by ID */
    @DeleteMapping("/deleteItem/{serverItemID}")
    public ResponseEntity<?> deleteServerItem(@PathVariable Long serverItemID) {
        logger.info("Delete request received for ServerItem ID: {}", serverItemID);

        if (!serverItemRepository.existsById(serverItemID)) {
            logger.error("Delete failed: ServerItem ID {} not found.", serverItemID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Server item not found"));
        }

        serverItemRepository.deleteById(serverItemID);
        logger.info("ServerItem ID {} deleted successfully.", serverItemID);
        return ResponseEntity.ok(Map.of("message", "Server item deleted successfully"));
    }

    /** ✅ EDIT an existing Server Item */
    @PutMapping("/editItem/{serverItemID}")
    public ResponseEntity<?> editServerItem(@PathVariable Long serverItemID, @RequestBody ServerItemModel updatedItem) {
        logger.info("Edit request received for ServerItem ID: {}", serverItemID);

        return serverItemRepository.findById(serverItemID)
                .map(existingItem -> {
                    boolean updated = false;
                    StringBuilder updatedFields = new StringBuilder("Updated fields: ");

                    if (updatedItem.getServerItemName() != null && !updatedItem.getServerItemName().trim().isEmpty()
                            && !updatedItem.getServerItemName().equals(existingItem.getServerItemName())) {
                        existingItem.setServerItemName(updatedItem.getServerItemName());
                        updatedFields.append("serverItemName, ");
                        updated = true;
                    }
                    if (updatedItem.getServerItemDescription() != null && !updatedItem.getServerItemDescription().trim().isEmpty()
                            && !updatedItem.getServerItemDescription().equals(existingItem.getServerItemDescription())) {
                        existingItem.setServerItemDescription(updatedItem.getServerItemDescription());
                        updatedFields.append("serverItemDescription, ");
                        updated = true;
                    }
                    if (updatedItem.getServerItemType() != null && !updatedItem.getServerItemType().trim().isEmpty()
                            && !updatedItem.getServerItemType().equals(existingItem.getServerItemType())) {
                        existingItem.setServerItemType(updatedItem.getServerItemType());
                        updatedFields.append("serverItemType, ");
                        updated = true;
                    }
                    if (updatedItem.getItemCost() > 0 && updatedItem.getItemCost() != existingItem.getItemCost()) {
                        existingItem.setItemCost(Math.max(0, updatedItem.getItemCost())); // ✅ Prevent negative cost
                        updatedFields.append("itemCost, ");
                        updated = true;
                    }

                    if (!updated) {
                        logger.info("Edit request for ServerItem ID {} did not change any fields.", serverItemID);
                        return ResponseEntity.ok(Map.of("message", "No changes detected."));
                    }

                    ServerItemModel savedItem = serverItemRepository.save(existingItem);
                    logger.info("ServerItem ID {} updated successfully. {}", serverItemID, updatedFields.toString());
                    return ResponseEntity.ok(Map.of("message", "Server item updated successfully", "updatedFields", updatedFields.toString()));
                })
                .orElseGet(() -> {
                    logger.error("Edit failed: ServerItem ID {} not found.", serverItemID);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Server item not found"));
                });
    }

    /** ✅ GET all Server Items */
    @GetMapping("/listItems")
    public ResponseEntity<List<ServerItemModel>> listAllServerItems() {
        logger.debug("Fetching all server items.");
        List<ServerItemModel> items = serverItemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    /** ✅ GET a single Server Item by ID */
    @GetMapping("/listItem/{serverItemID}")
    public ResponseEntity<?> getServerItemById(@PathVariable Long serverItemID) {
        logger.debug("Fetching ServerItem ID: {}", serverItemID);

        Optional<ServerItemModel> item = serverItemRepository.findById(serverItemID);

        if (item.isEmpty()) {
            logger.warn("Fetch failed: ServerItem ID {} not found.", serverItemID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Server item not found", "serverItemID", serverItemID));
        }

        return ResponseEntity.ok(item.get());
    }

}
