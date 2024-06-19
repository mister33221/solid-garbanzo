package com.kai.spring_boot_redis_practice;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void flush() {
//        flushDb() is deprecated
//        flushAll() is deprecated
//        TODO: find a way to flush keys
    }

    public long dbSize() {
        return stringRedisTemplate.getConnectionFactory().getConnection().dbSize();
    }

    public void saveWithExpire(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds);
    }

    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    public void saveWithExpireAt(String key, String value, long unixTime) {
        stringRedisTemplate.opsForValue().set(key, value, unixTime);
    }

    public long getExpireAt(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    public void saveIfAbsent(String key, String value) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public void saveIfAbsentWithExpire(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofDays(seconds));
    }

    public void saveIfAbsentWithExpireAt(String key, String value, long unixTime) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofDays(unixTime));
    }

    public void saveWithExpireAndSet(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds);
    }

    public void saveWithExpireAtAndSet(String key, String value, long unixTime) {
        stringRedisTemplate.opsForValue().set(key, value, unixTime);
    }

    public void saveWithExpireAndSetIfAbsent(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofDays(seconds));
    }

    public void saveWithExpireAtAndSetIfAbsent(String key, String value, long unixTime) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofDays(unixTime));
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

    public void setBit(String key, long offset, boolean value) {
        stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, long offset) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, offset));
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

    public void multiSetIfAbsent(String key1, String value1, String key2, String value2) {
        stringRedisTemplate.opsForValue().multiSetIfAbsent(Map.of(key1, value1, key2, value2));
    }


    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void saveHash(String key, String name, String description, Integer likes, Integer visitors) {
        stringRedisTemplate.opsForHash().put(key, "name", name);
        stringRedisTemplate.opsForHash().put(key, "description", description);
        stringRedisTemplate.opsForHash().put(key, "likes", likes.toString());
        stringRedisTemplate.opsForHash().put(key, "visitors", visitors.toString());
    }
}
