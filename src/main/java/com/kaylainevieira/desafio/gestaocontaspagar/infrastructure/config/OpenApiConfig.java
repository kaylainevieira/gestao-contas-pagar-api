package com.kaylainevieira.desafio.gestaocontaspagar.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API de Gestão de Contas a Pagar")
                        .description("API REST para gerenciar o ciclo de vida de contas a pagar, incluindo CRUD, alteração de situação, listagem filtrada e importação via CSV.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação Externa para Contas a Pagar")
                        .url("http://springdoc.org"))
                .components(new Components()
                        .addSecuritySchemes("basicScheme",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Autenticação HTTP Basic. Use 'admin'/'admin' para testar.")
                        ));
    }
}