package com.kai.spring_boot_redis_practice;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MyService {

    private final StringRedisTemplate stringRedisTemplate;

    public MyService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void save(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void update(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void saveWithExpire(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public long getExpire(String key) {
        Optional<Long> duration = Optional.ofNullable(stringRedisTemplate.getExpire(key)); // 因為 getExpire() 回傳時，可能已經過期了，所以用 Optional 來處理。
        return duration.orElse(0L); // 如果 duration 是 null(已經過期所以拿不到)，則回傳 0。
    }

    public void saveIfAbsent(String key, String value) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public void increment(String key, long delta) {
        stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public void decrement(String key, long delta) {
        stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    public long append(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value);
    }

    public String getRange(String key, long start, long end) {
        return stringRedisTemplate.opsForValue().get(key, start, end);
    }

    public void setRange(String key, String value, long offset) {
        stringRedisTemplate.opsForValue().set(key, value, offset);
    }

    public void multiSet(String key1, String value1, String key2, String value2) {
        stringRedisTemplate.opsForValue().multiSet(Map.of(key1, value1, key2, value2));
    }

    public List<String> multiGet(String key1, String key2) {
        List<String> keys = new ArrayList<>();
        keys.add(key1);
        keys.add(key2);
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("likes", likes.toString());
        map.put("visitors", visitors.toString());
        stringRedisTemplate.opsForHash().putAll(key, map);
    }


    public String getHashValue(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }
}
