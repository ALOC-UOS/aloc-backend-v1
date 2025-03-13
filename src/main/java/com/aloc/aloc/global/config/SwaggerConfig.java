package com.aloc.aloc.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
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
      bearerFormat = "JWT")
})
public class SwaggerConfig {}
