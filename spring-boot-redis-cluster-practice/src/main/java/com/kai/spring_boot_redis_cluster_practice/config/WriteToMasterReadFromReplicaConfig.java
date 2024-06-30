package com.kai.spring_boot_redis_cluster_practice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

@Configuration
public class WriteToMasterReadFromReplicaConfig {

    @Value("${ip}")
    private String ip;

    @Value("${redis.password}")
    private String redisPassword;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(REPLICA_PREFERRED) // 設定讀寫分離
                .build();

        RedisClusterConfiguration serverConfig = new RedisClusterConfiguration();
        serverConfig.clusterNode(ip, 7001);
        serverConfig.clusterNode(ip, 7002);
        serverConfig.clusterNode(ip, 7003);
        serverConfig.clusterNode(ip, 7004);
        serverConfig.clusterNode(ip, 7005);
        serverConfig.clusterNode(ip, 7006);
        serverConfig.setPassword(redisPassword);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

}
