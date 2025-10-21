package com.seoulotakus.takumapbe.global.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/api").description("TAKUMAP API")))
                .info(new Info()
                        .title("TAKUMAP API")
                        .version("1.0.0")
                        .description("TAKUMAP API 문서")
                        .contact(new Contact()
                                .name("Seoul Otakus")));
    }
}
