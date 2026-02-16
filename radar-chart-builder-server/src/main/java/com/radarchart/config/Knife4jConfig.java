package com.radarchart.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("雷达图生成器API")
                        .version("1.0.0")
                        .description("雷达图生成器后端API文档")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")));
    }

    @Bean
    public GroupedOpenApi groupedOpenAPI() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/api/**")
                .build();
    }
}
