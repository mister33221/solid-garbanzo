package com.kai.spring_boot_redis_cluster_practice.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyService {

    private static final Logger log = LoggerFactory.getLogger(MyService.class);
    private final StringRedisTemplate stringRedisTemplate;

    public MyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public void save(String key, String value) {

        stringRedisTemplate.opsForValue().set(key, value);

        log.info("Saved Key: {}, Value: {}", key, value);
    }

    public String get(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);

        log.info("Key: {}, Value: {}", key, value);

        return value;
    }

    public String flush() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
        log.info("Flushed all keys");
        return "Flushed all keys";
    }

    public List<String> getAll() {
        List<String> result = new ArrayList<>();
        stringRedisTemplate.keys("*").forEach(key -> {
            String keyWithValues = key + ": " + stringRedisTemplate.opsForValue().get(key);
            result.add(keyWithValues);
        });

        log.info("All keys and values: {}", result);

        return result;
    }
}
