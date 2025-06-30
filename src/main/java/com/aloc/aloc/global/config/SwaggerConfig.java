package com.aloc.aloc.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Value("${app.base-url}")
  private String baseUrl;

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder().group("Service API").pathsToMatch("/api/**").build();
  }

  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder().group("Admin API").pathsToMatch("/admin/**").build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("OPEN-ALOC API").version("v1"))
        .addSecurityItem(new SecurityRequirement().addList("JWT Auth"))
        .addSecurityItem(new SecurityRequirement().addList("Google OAuth2"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "JWT Auth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecuritySchemes(
                    "Google OAuth2",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            new OAuthFlows()
                                .authorizationCode(
                                    new OAuthFlow()
                                        .authorizationUrl(
                                            baseUrl + "/oauth2/authorization/google?from=swagger")
                                        .tokenUrl("https://oauth2.googleapis.com/token")
                                        .scopes(
                                            new Scopes()
                                                .addString("profile", "Google profile access")
                                                .addString("email", "Google email access"))))));
  }
}
