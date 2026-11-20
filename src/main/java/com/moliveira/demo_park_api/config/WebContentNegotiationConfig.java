package com.moliveira.demo_park_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JasperReports 7 brings jackson-dataformat-xml onto the classpath, which makes
 * Spring MVC answer with XML when a client sends "Accept: *&#47;*" (or no Accept
 * header). This API is JSON-only, so JSON is made the default response type.
 */
@Configuration
public class WebContentNegotiationConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
