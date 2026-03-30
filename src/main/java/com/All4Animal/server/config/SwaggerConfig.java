package com.All4Animal.server.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Animal API",
                version = "v1",
                description = "유기동물 입양 서비스"
        )
)
public class SwaggerConfig {
}
