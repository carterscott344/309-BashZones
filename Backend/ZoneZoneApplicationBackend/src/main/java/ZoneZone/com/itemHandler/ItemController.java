package ZoneZone.com.itemHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
public class ItemController {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // POST: /____/createItem
    // @PostMapping("/____/createItem")
    public ResponseEntity<?> createItem(@RequestBody ItemModel item) {
        return null; // stub
    }

    // DELETE: /____/deleteItem/{itemName}
    // @PostMapping("/____/deleteItem/{itemName}")
    public ResponseEntity<?> deleteItem(@RequestBody ItemModel item) {
        return null; // stub
    }

    // PUT: /____/updateItem/{itemName}
    // @PostMapping("/____/updateItem/{itemName}")
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

    // GET: /____/listItem/{itemName} - Get one item by name
    // @GetMapping("/____/listItem/{itemName}")
    public ResponseEntity<?> getItem(@PathVariable String itemName) {
        Optional<ItemModel> itemOptional = itemRepository.findById(itemName);
        return itemOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}