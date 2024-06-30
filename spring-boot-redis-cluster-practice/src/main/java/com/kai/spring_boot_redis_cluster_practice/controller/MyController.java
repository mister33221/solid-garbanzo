package com.kai.spring_boot_redis_cluster_practice.controller;

import com.kai.spring_boot_redis_cluster_practice.service.MyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {

    private final MyService service;

    public MyController(MyService service) {
        this.service = service;
    }

    @Operation(
            summary = "Hello World",
            description = "Returns a simple Hello World message"
    )
    @Tag(name = "Hello World")
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @Operation(
            summary = "Save a Base-operation pair",
            description = "Save a Base-operation pair"
    )
    @Tag(name = "Base-operation")
    @GetMapping("/save")
    public void save(@Parameter(description = "The key") String key, @Parameter(description = "The value") String value) {
        service.save(key, value);
    }

    @Operation(
            summary = "Get a value by key",
            description = "Gets a value by key"
    )
    @Tag(name = "Base-operation")
    @GetMapping("/get")
    public String get(@Parameter(description = "The key") String key) {
        return service.get(key);
    }

    @Tag(name = "Base-operation")
    @Operation(
            summary = "Get all the keys and values in Redis Cluster",
            description = "Gets all the keys and values in Redis Cluster"
    )
    @GetMapping("/getAll")
    public List<String> getAll() {
        return service.getAll();
    }

    @Tag(name = "Base-operation")
    @Operation(
            summary = "Flush all the data in Redis Cluster",
            description = "Deletes all the data in Redis Cluster"
    )
    @Tag(name = "Base-operation")
    @GetMapping("/flush")
    public String flush() {
        return service.flush();
    }
}
