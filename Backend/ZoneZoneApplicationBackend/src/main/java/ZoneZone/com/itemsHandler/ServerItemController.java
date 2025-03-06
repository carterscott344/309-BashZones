package ZoneZone.com.itemsHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/serverItems")
public class ServerItemController {

    private final ServerItemRepository serverItemRepository;

    // Constructor for Dependency Injection
    public ServerItemController(ServerItemRepository serverItemRepository) {
        this.serverItemRepository = serverItemRepository;
    }

    /**
     * 📌 CREATE a new Server Item
     * Endpoint: POST /serverItems/createItem
     */
    @PostMapping("/createItem")
    public ResponseEntity<ServerItemModel> createServerItem(@RequestBody ServerItemModel newItem) {
        if (serverItemRepository.existsByServerItemName(newItem.getServerItemName())) {
            return ResponseEntity.badRequest().body(null);
        }
        ServerItemModel savedItem = serverItemRepository.save(newItem);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * 📌 DELETE a Server Item by ID
     * Endpoint: DELETE /serverItems/deleteItem/{serverItemID}
     */
    @DeleteMapping("/deleteItem/{serverItemID}")
    public ResponseEntity<Void> deleteServerItem(@PathVariable Long serverItemID) {
        if (!serverItemRepository.existsById(serverItemID)) {
            return ResponseEntity.notFound().build();
        }
        serverItemRepository.deleteById(serverItemID);
        return ResponseEntity.noContent().build();
    }

    /**
     * 📌 EDIT an existing Server Item
     * Endpoint: PUT /serverItems/editItem/{serverItemID}
     */
    @PutMapping("/editItem/{serverItemID}")
    public ResponseEntity<ServerItemModel> editServerItem(@PathVariable Long serverItemID,
                                                          @RequestBody ServerItemModel updatedItem) {
        return serverItemRepository.findById(serverItemID)
                .map(existingItem -> {
                    if(updatedItem.getServerItemName() != null) {
                        existingItem.setServerItemName(updatedItem.getServerItemName());
                    }
                    if(updatedItem.getServerItemDescription() != null) {
                        existingItem.setServerItemDescription(updatedItem.getServerItemDescription());
                    }
                    if(updatedItem.getServerItemType() != null) {
                        existingItem.setServerItemType(updatedItem.getServerItemType());
                    }
                    if(updatedItem.getItemCost() != 0) {
                        existingItem.setItemCost(updatedItem.getItemCost());
                    }

                    ServerItemModel savedItem = serverItemRepository.save(existingItem);
                    return ResponseEntity.ok(savedItem);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 📌 GET all Server Items
     * Endpoint: GET /serverItems/listItems
     */
    @GetMapping("/listItems")
    public ResponseEntity<List<ServerItemModel>> listAllServerItems() {
        List<ServerItemModel> items = serverItemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    /**
     * 📌 GET a single Server Item by ID
     * Endpoint: GET /serverItems/listItem/{serverItemID}
     */
    @GetMapping("/listItem/{serverItemID}")
    public ResponseEntity<ServerItemModel> getServerItemById(@PathVariable Long serverItemID) {
        Optional<ServerItemModel> item = serverItemRepository.findById(serverItemID);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
