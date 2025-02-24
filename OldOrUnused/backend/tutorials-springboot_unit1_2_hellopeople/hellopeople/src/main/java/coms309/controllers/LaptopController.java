package coms309.controllers;

import coms309.models.Laptop;
import coms309.repository.LaptopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/laptops")
public class LaptopController {

    private final LaptopRepository laptopRepository;

    public LaptopController(LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }

    // CREATE: POST /laptops
    @PostMapping
    public Laptop createLaptop(@RequestBody Laptop laptop) {
        return laptopRepository.save(laptop);
    }

    // READ: GET /laptops/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Laptop> getLaptopById(@PathVariable Long id) {
        Optional<Laptop> laptopOptional = laptopRepository.findById(id);
        return laptopOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // LIST: GET /laptops
    @GetMapping
    public List<Laptop> listLaptops() {
        return laptopRepository.findAll();
    }
}
