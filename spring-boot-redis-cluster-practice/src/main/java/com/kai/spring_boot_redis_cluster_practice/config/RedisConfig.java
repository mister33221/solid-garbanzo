package com.kai.spring_boot_redis_cluster_practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration()
                .clusterNode("127.0.0.1", 7000)
                .clusterNode("127.0.0.1", 7001)
                .clusterNode("127.0.0.1", 7002)
                .clusterNode("127.0.0.1", 7003)
                .clusterNode("127.0.0.1", 7004)
                .clusterNode("127.0.0.1", 7005);
        return new LettuceConnectionFactory(clusterConfiguration);
    }
}
