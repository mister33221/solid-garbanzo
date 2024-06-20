package com.kai.spring_boot_redis_practice.controller;

import com.kai.spring_boot_redis_practice.publisher.RedisPublisher;
import com.kai.spring_boot_redis_practice.service.PubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PubController {

    public PubService pubService;

    public PubController(PubService pubService) {
        this.pubService = pubService;
    }

    @Operation(summary = "Publish a message to a topic")
    @GetMapping("/publish")
    public String publish(String topic, String message) {
        pubService.publish(topic, message);
        return "Message published";
    }

}
