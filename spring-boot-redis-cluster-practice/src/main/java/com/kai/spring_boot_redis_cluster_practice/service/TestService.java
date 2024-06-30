package com.kai.spring_boot_redis_cluster_practice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);
    private final StringRedisTemplate redisTemplate;

    public TestService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Long> testSharding(Integer keyCount) {

        for (int i = 0; i < keyCount; i++) {
            redisTemplate.opsForValue().set("key" + i, "value" + i);
        }

        Map<String, Long> nodePortAndKeyCounts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            String key = "key" + i;
            RedisClusterConnection clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
            int slot = clusterConnection.clusterGetSlotForKey(key.getBytes()); // Redis cluster 會根據 key 的哈希值來分配槽
            String nodePort = String.valueOf(clusterConnection.clusterGetNodeForSlot(slot).getPort()); // 根據槽來獲取節點
            nodePortAndKeyCounts.put(nodePort, nodePortAndKeyCounts.getOrDefault(nodePort, 0L) + 1);
        }

        logger.info("""
                Node ports and their key counts:
                {}
                """, nodePortAndKeyCounts);

        return nodePortAndKeyCounts;

    }

    public String testAvailability() {

//    1. 先寫入一筆數據
        String key = "availabilityKey";
        String value = "availabilityValue";
        redisTemplate.opsForValue().set(key, value);

        logger.info("Written key: {}, value: {}", key, value);

//    2. 確認他在哪個節點
        RedisClusterConnection clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
        int slot = clusterConnection.clusterGetSlotForKey(key.getBytes());
        String nodePort = String.valueOf(clusterConnection.clusterGetNodeForSlot(slot).getPort());

        logger.info("The key is stored in the node with port: {}", nodePort);

        return "The key is stored in the node with port: " + nodePort + ", Please manually shut down the node by using docker stop command. Then reboot the app and use the testAvailability2 API.";
    }

    public String testAvailability2() {
        String key = "availabilityKey";

        String retrievedValue = redisTemplate.opsForValue().get(key);

        RedisClusterConnection clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
        int slot = clusterConnection.clusterGetSlotForKey(key.getBytes());
        RedisClusterNode node = clusterConnection.clusterGetNodeForSlot(slot);
        int nodePort = node.getPort();

        logger.info("The key is stored in the node with port: {}, value: {}", nodePort, retrievedValue);

        return "The key is stored in the node with port: " + nodePort + ", value: " + retrievedValue;

    }


}
