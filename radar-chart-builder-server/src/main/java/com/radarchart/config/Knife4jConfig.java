package com.radarchart.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.models.OpenAPI;
import org.springdoc.core.models.info.Contact;
import org.springdoc.core.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenAPI customOpenAPI() {
        return new GroupedOpenApi()
                .info(new Info()
                        .title("雷达图生成器API")
                        .version("1.0.0")
                        .description("雷达图生成器后端API文档")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")))
                .tags(Arrays.asList(
                        new Tag()
                                .name("用户认证")
                                .description("用户注册、登录相关接口")));
    }
}
