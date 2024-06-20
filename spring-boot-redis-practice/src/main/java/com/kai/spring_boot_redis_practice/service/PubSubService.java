package com.kai.spring_boot_redis_practice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

    private final StringRedisTemplate stringRedisTemplate;

    public PubSubService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String myTopic, String message) {
        stringRedisTemplate.convertAndSend(myTopic, message);
    }
}
