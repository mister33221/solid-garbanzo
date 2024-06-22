package com.kai.spring_boot_redis_cluster_practice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @Operation(summary = "Hello World", description = "Returns a simple Hello World message")
    @Tag(name = "Hello World")
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @Operation(summary = "Save", description = "Save a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/save")
    public void save(@Parameter(description = "The key") String key, @Parameter(description = "The value") String value) {
        service.save(key, value);
    }

    @Operation(summary = "Get", description = "Gets a value by key")
    @Tag(name = "Key-Value")
    @GetMapping("/get")
    public String get(@Parameter(description = "The key") String key) {
        return service.get(key);
    }



}
