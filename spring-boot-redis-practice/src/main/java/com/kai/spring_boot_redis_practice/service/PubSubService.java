package com.kai.spring_boot_redis_practice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PubService {

    private final StringRedisTemplate stringRedisTemplate;

    public PubService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String myTopic, String message) {
        stringRedisTemplate.convertAndSend(myTopic, message);
        stringRedisTemplate.opsForValue().set(myTopic, message);
    }
}
