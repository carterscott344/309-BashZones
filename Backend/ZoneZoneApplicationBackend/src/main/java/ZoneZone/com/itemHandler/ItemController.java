package ZoneZone.com.itemHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
public class ItemController {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // POST: /userItems/createItem
    @PostMapping("/userItems/createItem")
    public ResponseEntity<?> createItem(@RequestBody ItemModel item) {
        try {
            ItemModel savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating item: " + e.getMessage());
        }
    }

    // DELETE: /userItems/deleteItem/{itemName}
    @DeleteMapping("/userItems/deleteItem/{itemName}")
    public ResponseEntity<?> deleteItem(@PathVariable String itemName) {
        if (itemRepository.existsById(itemName)) {
            itemRepository.deleteById(itemName);
            return ResponseEntity.ok().body("{\"message\": \"User deleted successfully\"}"); // Return JSON message
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\": \"Item not found\"}"); // Handle 404 properly
    }

    // PUT: /userItems/updateItem/{itemName}
    @PutMapping("/userItems/updateItem/{itemName}")
    public ResponseEntity<?> updateItem(@RequestBody String itemName, @RequestBody ItemModel updatedItem) {
        Optional<ItemModel> itemOptional = itemRepository.findById(itemName);
        if (itemOptional.isPresent()) {
            ItemModel item = itemOptional.get();

            // Update fields (adjust as needed)
            item.setItemName(updatedItem.getItemName());
            item.setItemCost(updatedItem.getItemCost());
            item.setItemType(updatedItem.getItemType());

            ItemModel savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        }
        return ResponseEntity.notFound().build();
    }

    // GET: /userItems/listItem/{itemName} - Get one item by name
    @GetMapping("/userItems/listItem/{itemName}")
    public ResponseEntity<?> getItem(@PathVariable String itemName) {
        Optional<ItemModel> itemOptional = itemRepository.findById(itemName);
        return itemOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}