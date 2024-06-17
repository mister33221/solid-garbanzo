package com.kai.spring_boot_redis_practice;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    // Swagger annotation
    @Operation(summary = "Hello World", description = "Returns a simple Hello World message")
    public String helloWorld() {
        return "Hello World!";
    }
}
