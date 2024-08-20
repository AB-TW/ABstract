package cn.dpc.ab.api.file.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * WebConfig configures the web application.
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {
    @Autowired
    private FileProperties fileProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileProperties.getUploadDir() + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

}
