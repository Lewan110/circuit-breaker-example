package com.adrian.circuit.breaker;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class Controller {

    private final UserService service;

    @GetMapping("/users")
    public List<User> users() {
        return service.getUsers();
    }

}