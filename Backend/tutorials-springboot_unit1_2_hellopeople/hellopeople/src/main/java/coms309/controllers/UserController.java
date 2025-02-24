package coms309.controllers;

import coms309.models.User;
import coms309.models.Laptop;
import coms309.repository.UserRepository;
import coms309.repository.LaptopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final LaptopRepository laptopRepository;

    public UserController(UserRepository userRepository, LaptopRepository laptopRepository) {
        this.userRepository = userRepository;
        this.laptopRepository = laptopRepository;
    }

    // CREATE: POST /users (Requires existing Laptop ID)
    @PostMapping
    public ResponseEntity<?> createUser(@RequestParam Long laptopId, @RequestBody User user) {
        Optional<Laptop> laptopOptional = laptopRepository.findById(laptopId);
        if (laptopOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Laptop with ID " + laptopId + " does not exist.");
        }

        user.setLaptop(laptopOptional.get());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // READ: GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE: PUT /users/{id} (Optional Laptop Update)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) Long laptopId,
            @RequestBody User updatedUser) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = userOptional.get();
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        if (laptopId != null) {
            Optional<Laptop> laptopOptional = laptopRepository.findById(laptopId);
            if (laptopOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            existingUser.setLaptop(laptopOptional.get());
        }

        User savedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);
    }

    // DELETE: DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // LIST: GET /users
    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
