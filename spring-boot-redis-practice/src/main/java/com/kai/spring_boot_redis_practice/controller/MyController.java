package com.kai.spring_boot_redis_practice.controller;

import com.kai.spring_boot_redis_practice.service.MyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Operation(summary = "Delete", description = "Deletes a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/delete")
    public void delete(String key) {
        service.delete(key);
    }

    @Operation(summary = "Update", description = "Updates a key-value pair")
    @Tag(name = "Key-Value")
    @GetMapping("/update")
    public void update(String key, String value) {
        service.update(key, value);
    }

    @Operation(summary = "Exists", description = "Checks if a key exists")
    @Tag(name = "Key-Value")
    @GetMapping("/exists")
    public boolean exists(String key) {
        return service.exists(key);
    }

    @Operation(summary = "Save with Expire", description = "Save a key-value pair with an expiration time")
    @Tag(name = "Key-Value")
    @GetMapping("/saveWithExpire")
    public void saveWithExpire(String key, String value, long seconds) {
        service.saveWithExpire(key, value, seconds);
    }

    @Operation(summary = "Get Expire", description = "Gets the expiration time of a key")
    @Tag(name = "Key-Value")
    @GetMapping("/getExpire")
    public long getExpire(String key) {
        return service.getExpire(key);
    }

    @Operation(summary = "Save If Absent", description = "Save a key-value pair if the key does not exist")
    @Tag(name = "Key-Value")
    @GetMapping("/saveIfAbsent")
    public void saveIfAbsent(String key, String value) {
        service.saveIfAbsent(key, value);
    }

    @Operation(summary = "Increment", description = "Increments a key by a delta")
    @Tag(name = "Counter")
    @GetMapping("/increment")
    public void increment(String key, long delta) {
        service.increment(key, delta);
    }

    @Operation(summary = "Decrement", description = "Decrements a key by a delta")
    @Tag(name = "Counter")
    @GetMapping("/decrement")
    public void decrement(String key, long delta) {
        service.decrement(key, delta);
    }

    @Operation(summary = "Append", description = "Appends a value to a key")
    @Tag(name = "String")
    @GetMapping("/append")
    public long append(String key, String value) {
        return service.append(key, value);
    }

    @Operation(summary = "Get Range", description = "Gets a range of values from a key")
    @Tag(name = "Other")
    @GetMapping("/getRange")
    public String getRange(String key, long start, long end) {
        return service.getRange(key, start, end);
    }

    @Operation(summary = "Set Range", description = "Sets a range of values to a key")
    @Tag(name = "Other")
    @GetMapping("/setRange")
    public void setRange(String key, String value, long offset) {
        service.setRange(key, value, offset);
    }

    @Operation(summary = "Multi Save", description = "Save multiple key-value pairs")
    @Tag(name = "Key-Value")
    @GetMapping("/multiSave")
    public void multiSave(String key1, String value1, String key2, String value2) {
        service.multiSet(key1, value1, key2, value2);
    }

    @Operation(summary = "Multi Get", description = "Gets multiple values by key")
    @Tag(name = "Key-Value")
    @GetMapping("/multiGet")
    public List<String> multiGet(String key1, String key2) {
        return service.multiGet(key1, key2);
    }

    @Operation(summary = "Save a list", description = "Save a list")
    @Tag(name = "List")
    @PostMapping("/saveList")
    public void saveList(String key, @RequestBody ArrayList<String> value) {
        service.saveList(key, value);
    }

    @Operation(summary = "Get a list", description = "Gets a list by key")
    @Tag(name = "List")
    @GetMapping("/getList")
    public List<String> getList(String key) {
        return service.getList(key);
    }

    @Operation(summary = "Add a value to the end of a list", description = "Add a value to the end of a list")
    @Tag(name = "List")
    @GetMapping("/addAValueToEndOfList")
    public void addAValueToEndOfList(String key, String value) {
        service.addAValueToEndOfList(key, value);
    }

    @Operation(summary = "Add a value to the beginning of a list", description = "Add a value to the beginning of a list")
    @Tag(name = "List")
    @GetMapping("/addAValueToBeginningOfList")
    public void addAValueToBeginningOfList(String key, String value) {
        service.addAValueToBeginningOfList(key, value);
    }

    @Operation(summary = "Pop a value from the end of a list", description = "Pop a value from the end of a list. After popping, the value is removed from the list.")
    @Tag(name = "List")
    @GetMapping("/popAValueFromEndOfList")
    public String popAValueFromEndOfList(String key) {
        return service.popAValueFromEndOfList(key);
    }

    @Operation(summary = "Pop a value from the beginning of a list", description = "Pop a value from the beginning of a list. After popping, the value is removed from the list.")
    @Tag(name = "List")
    @GetMapping("/popAValueFromBeginningOfList")
    public String popAValueFromBeginningOfList(String key) {
        return service.popAValueFromBeginningOfList(key);
    }

    @Operation(summary = "Get a value from a list by index", description = "Get a value from a list by index")
    @Tag(name = "List")
    @GetMapping("/getAValueFromListByIndex")
    public String getAValueFromListByIndex(String key, long index) {
        return service.getAValueFromListByIndex(key, index);
    }

    @Operation(summary = "Remove a value from a list by index", description = "Remove a value from a list by index")
    @Tag(name = "List")
    @GetMapping("/removeAValueFromListByIndex")
    public void removeAValueFromListByIndex(String key, long index, String value) {
        service.removeAValueFromListByIndex(key, index, value);
    }

    @Operation(summary = "Remove the list", description = "Remove the list")
    @Tag(name = "List")
    @GetMapping("/removeList")
    public void removeList(String key) {
        service.removeList(key);
    }

    @Operation(summary = "Save a set", description = "Save a set")
    @Tag(name = "Set")
    @PostMapping("/saveSet")
    public void saveSet(String key, @RequestBody Set<String> value) {
        service.saveSet(key, value);
    }

    @Operation(summary = "Get a set", description = "Gets a set by key")
    @Tag(name = "Set")
    @GetMapping("/getSet")
    public List<String> getSet(String key) {
        return service.getSet(key);
    }

    @Operation(summary = "Remove a value from a set", description = "Remove a value from a set")
    @Tag(name = "Set")
    @GetMapping("/removeAValueFromSet")
    public void removeAValueFromSet(String key, String value) {
        service.removeAValueFromSet(key, value);
    }

    @Operation(summary = "Compare two sets and get the difference", description = "Compare two sets and get the difference")
    @Tag(name = "Set")
    @GetMapping("/difference")
    public Set<String> difference(String key1, String key2) {
        return service.difference(key1, key2);
    }

    @Operation(summary = "Save Hash", description = "Save a hash")
    @Tag(name = "Hash")
    @GetMapping("/saveHash")
    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        service.saveHash(key, name, description, likes, visitors);
    }

    @Operation(summary = "Get hash", description = "Gets a hash by key")
    @Tag(name = "Hash")
    @GetMapping("/getHash")
    public Map<Object, Object> getHash(String key) {
        return service.getHash(key);
    }

    @Operation(summary = "Get a field value from a hash", description = "Gets a field value from a hash")
    @Tag(name = "Hash")
    @GetMapping("/getHashFieldValue")
    public String getHashFieldValue(String key, String field) {
        return service.getHashValue(key, field);
    }

    @Operation(summary = "Delete a field from a hash", description = "Delete a field from a hash")
    @Tag(name = "Hash")
    @GetMapping("/deleteHashField")
    public void deleteHashField(String key, String field) {
        service.deleteHashField(key, field);
    }

    @Operation(summary = "Exists a field in a hash", description = "Exists a field in a hash")
    @Tag(name = "Hash")
    @GetMapping("/existsHashField")
    public boolean existsHashField(String key, String field) {
        return service.existsHashField(key, field);
    }

    @Operation(summary = "Delete a hash", description = "Delete a hash")
    @Tag(name = "Hash")
    @GetMapping("/deleteHash")
    public void deleteHash(String key) {
        service.deleteHash(key);
    }





}
