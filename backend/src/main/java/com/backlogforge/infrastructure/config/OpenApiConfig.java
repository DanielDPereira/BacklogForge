package com.backlogforge.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração da documentação OpenAPI 3.0 / Swagger UI para a API do BacklogForge.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BacklogForge API")
                        .version("1.0.0")
                        .description("API REST para geração assistida por IA de Product Backlogs a partir de documentação em PDF (vetoriais ou escaneados via OCR multimodal) e texto livre. Oferece exportação em Markdown (.md) e PDF (.pdf).")
                        .contact(new Contact()
                                .name("BacklogForge Team")
                                .url("https://github.com/DanielDPereira/BacklogForge"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento Local")
                ));
    }
}
