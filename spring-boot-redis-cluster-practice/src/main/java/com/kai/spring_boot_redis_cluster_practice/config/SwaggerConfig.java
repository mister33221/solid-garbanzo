package com.kai.spring_boot_redis_cluster_practice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot integration with single node Redis practice",
                version = "0.0"
        )
)
// 如果有使用JWT的話 就可以使用以下這些 來使 swagger頁面產生一個 Authorize的按鈕，在其中輸入token就可以使用那些被鎖住的功能
//@SecurityScheme(
//        name = "Bearer Authentication",
//        type = SecuritySchemeType.HTTP,
//        bearerFormat = "JWT",
//        scheme = "bearer"
//)
@Configuration
public class SwaggerConfig {
}
