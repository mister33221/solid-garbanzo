package com.kai.spring_boot_redis_practice.controller;

import com.kai.spring_boot_redis_practice.service.PubSubService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PubSubController {

    public PubSubService pubService;

    public PubSubController(PubSubService pubService) {
        this.pubService = pubService;
    }

    @Operation(summary = "Publish a message to a topic")
    @GetMapping("/publish")
    public String publish(String topic, String message) {
        pubService.publish(topic, message);
        return "Message published";
    }

}
