package com.kai.spring_boot_redis_cluster_practice.service;

import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TestService {

    private final StringRedisTemplate redisTemplate;

    public TestService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Long> testSharding() {

        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForValue().set("key" + i, "value" + i);
        }

        Map<String, Long> nodeCounts = new HashMap<>();
        redisTemplate.getConnectionFactory().getClusterConnection().clusterGetNodes().forEach(node -> {
            Long keyCount = redisTemplate.getConnectionFactory().getClusterConnection().serverCommands().dbSize();
            nodeCounts.put(node.toString(), keyCount);
        });

        return nodeCounts;

    }

    public String testAvailability() {


        String key = "testKey";
        String value = "testValue";

        redisTemplate.opsForValue().set(key, value);

//      在這裡手動關閉一個主節點
//      指令是 docker stop redis-cluster-practice_redis-cluster-node-0_1

        String retrievedValue = redisTemplate.opsForValue().get(key);

        return "Original value: " + value + ", Retrieved value: " + retrievedValue;


    }

    public String testAutomaticFailover() throws InterruptedException {
        String key1 = "failoverKey1";
        String value1 = "failoverValue1";

        redisTemplate.opsForValue().set(key1, value1);

        // 在這裡手動關閉一個主節點

        Thread.sleep(10000); // 等待10秒，讓故障轉移發生

        String key2 = "failoverKey2";
        String value2 = "failoverValue2";
        redisTemplate.opsForValue().set(key2, value2);

        String retrievedValue1 = redisTemplate.opsForValue().get(key1);
        String retrievedValue2 = redisTemplate.opsForValue().get(key2);

        return "Key1: " + retrievedValue1 + ", Key2: " + retrievedValue2;
    }

    public Map<String, String> testConsistentHashing() {
        String[] keys = {"key1", "key2", "key3", "key4", "key5"};

        for (String key : keys) {
            redisTemplate.opsForValue().set(key, "value");
        }

        Map<String, String> keyLocations = new HashMap<>();
        for (String key : keys) {
//            TODO
//            RedisClusterNode node = redisTemplate.getConnectionFactory().getClusterConnection().keyCommands().nodeForKey(key.getBytes());
//            keyLocations.put(key, node.toString());
        }

        return keyLocations;
    }

    public String testReadWriteSplitting() {
        String key = "readWriteSplittingKey";
        String value = "readWriteSplittingValue";

        redisTemplate.opsForValue().set(key, value);

        String retrievedValue = redisTemplate.opsForValue().get(key);

        return "Original value: " + value + ", Retrieved value: " + retrievedValue;
    }
}
