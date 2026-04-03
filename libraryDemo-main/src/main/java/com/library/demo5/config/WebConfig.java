package com.library.demo5.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/template/**", "/css/**", "/js/**")
                .addResourceLocations("classpath:/static/template/", "classpath:/static/css/", "classpath:/static/js/")
                .setCachePeriod(86400);
        
        // Fallback for all static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(86400);
    }
}
