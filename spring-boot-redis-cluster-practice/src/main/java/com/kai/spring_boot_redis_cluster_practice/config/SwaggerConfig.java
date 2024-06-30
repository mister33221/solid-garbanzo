package com.kai.spring_boot_redis_cluster_practice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot integration with Redis Cluster practice",
                version = "0.0"
        )
)
@Configuration
public class SwaggerConfig {
}
