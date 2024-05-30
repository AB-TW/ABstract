package cn.dpc.ab;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.result.view.RedirectView;

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
        registry.addResourceHandler("/").addResourceLocations("file:static/index.html");
        registry.addResourceHandler("/v3-admin-vite/**").addResourceLocations("file:static/");
        registry.addResourceHandler("/**").addResourceLocations("file:static/");
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

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.defaultViews(new RedirectView("/index.html"));
    }
}
