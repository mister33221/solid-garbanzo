package com.kai.spring_boot_redis_cluster_practice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

//    public String testAvailability() {
//
//
//        String key = "testKey";
//        String value = "testValue";
//
//        redisTemplate.opsForValue().set(key, value);
//
////      在這裡手動關閉一個主節點
////      指令是 docker stop redis-cluster-practice_redis-cluster-node-0_1
//
//        String retrievedValue = redisTemplate.opsForValue().get(key);
//
//        return "Original value: " + value + ", Retrieved value: " + retrievedValue;
//
//
//    }
//
//    public String testAutomaticFailover() throws InterruptedException {
//        String key1 = "failoverKey1";
//        String value1 = "failoverValue1";
//
//        redisTemplate.opsForValue().set(key1, value1);
//
//        // 在這裡手動關閉一個主節點
//
//        Thread.sleep(10000); // 等待10秒，讓故障轉移發生
//
//        String key2 = "failoverKey2";
//        String value2 = "failoverValue2";
//        redisTemplate.opsForValue().set(key2, value2);
//
//        String retrievedValue1 = redisTemplate.opsForValue().get(key1);
//        String retrievedValue2 = redisTemplate.opsForValue().get(key2);
//
//        return "Key1: " + retrievedValue1 + ", Key2: " + retrievedValue2;
//    }
//
//    public Map<String, String> testConsistentHashing() {
//        String[] keys = {"key1", "key2", "key3", "key4", "key5"};
//
//        for (String key : keys) {
//            redisTemplate.opsForValue().set(key, "value");
//        }
//
//        Map<String, String> keyLocations = new HashMap<>();
//        for (String key : keys) {
////            TODO
////            RedisClusterNode node = redisTemplate.getConnectionFactory().getClusterConnection().keyCommands().nodeForKey(key.getBytes());
////            keyLocations.put(key, node.toString());
//        }
//
//        return keyLocations;
//    }
//
//    public Map<String, List<String>> testReadWriteSplitting(String key, String value, int readCount) {
//        Map<String, List<String>> result = new HashMap<>();
//
//        // Write operation
//        redisTemplate.opsForValue().set(key, value);
//        result.put("write", List.of(getNodeInfo()));
//
//        // Read operations
//        List<String> readNodes = new ArrayList<>();
//        for (int i = 0; i < readCount; i++) {
//            redisTemplate.opsForValue().get(key);
//            readNodes.add(getNodeInfo());
//        }
//        result.put("read", readNodes);
//
//        return result;
//    }
//
//    private String getNodeInfo() {
//        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
//        StatefulRedisClusterConnection<String, String> clusterConnection =
//                (StatefulRedisClusterConnection<String, String>) connectionFactory.getClusterConnection().getNativeConnection();
//        RedisClusterClient clusterClient = clusterConnection.
//        Partitions partitions = clusterClient.getPartitions();
//        String currentNode = partitions.getPartition(getSlot("test".getBytes())).getNodeId();
//        return currentNode;
//    }
//
//    private int getSlot(byte[] key) {
//        RedisClusterConnection clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
//        return clusterConnection.clusterGetSlotForKey(key);
//    }
}
