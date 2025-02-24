package coms309.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionController {

    // GET /oops - Endpoint to simulate an exception for testing error handling.
    @GetMapping("/oops")
    public void oops() {
        throw new RuntimeException("This is a simulated exception.");
    }
}
