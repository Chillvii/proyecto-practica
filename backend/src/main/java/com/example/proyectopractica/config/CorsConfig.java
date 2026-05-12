package com.example.proyectopractica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para desarrollo local.
 *
 * Sólo se activa con el perfil "dev". En producción, define una política CORS más
 * restrictiva acorde a tu dominio real; abrir CORS a localhost:4200 es razonable
 * en local pero no debe replicarse en producción tal cual.
 */
@Configuration
@Profile("dev")
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "Accept")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
