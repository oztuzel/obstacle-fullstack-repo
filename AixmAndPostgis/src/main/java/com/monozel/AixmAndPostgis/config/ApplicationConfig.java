package com.monozel.AixmAndPostgis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer () {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("POST","GET","PUT","DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };


    }

}
