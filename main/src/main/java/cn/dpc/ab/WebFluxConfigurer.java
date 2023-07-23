package cn.dpc.ab;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;

/**
 * WebFluxConfigurer.
 */
@Configuration
public class WebFluxConfigurer implements org.springframework.web.reactive.config.WebFluxConfigurer {

    /**
     * set static resource path outside jar.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        org.springframework.web.reactive.config.WebFluxConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/**").addResourceLocations("file:static/");
        registry.addResourceHandler("/").addResourceLocations("file:static/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .maxAge(3600)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedOriginPatterns("*");
    }
}
