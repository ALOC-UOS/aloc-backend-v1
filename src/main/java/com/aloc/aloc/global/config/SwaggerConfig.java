package com.aloc.aloc.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "OPEN-ALOC API", version = "v1"),
    security = {@SecurityRequirement(name = "JWT Auth")},
    servers = {
      @Server(url = "https://api.openaloc.store", description = "Production Server"),
      @Server(url = "http://localhost:8080", description = "Local Server")
    })
@SecuritySchemes({
  @SecurityScheme(
      name = "JWT Auth",
      type = SecuritySchemeType.HTTP,
      scheme = "bearer",
      bearerFormat = "JWT"),
  @SecurityScheme(
      name = "Google OAuth2",
      type = SecuritySchemeType.OAUTH2,
      flows =
          @OAuthFlows(
              authorizationCode =
                  @OAuthFlow(
                      authorizationUrl =
                          "http://localhost:8080/oauth2/authorization/google?from=swagger",
                      tokenUrl = "https://oauth2.googleapis.com/token",
                      scopes = {
                        @OAuthScope(name = "profile", description = "Google profile access"),
                        @OAuthScope(name = "email", description = "Google email access")
                      })))
})
public class SwaggerConfig {
  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder().group("Service API").pathsToMatch("/api/**").build();
  }

  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder().group("Admin API").pathsToMatch("/admin/**").build();
  }
}
