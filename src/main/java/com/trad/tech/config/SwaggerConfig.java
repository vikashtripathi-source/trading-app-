package com.trad.tech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI tradingApplicationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trading Application API")
                        .description("REST API for Trading Application with Kafka integration")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Trading Team")
                                .email("trading@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Development Server")
                ));
    }
}
