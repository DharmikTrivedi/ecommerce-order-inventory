package com.oims.config;

import com.oims.web.HeaderInterceptor;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HeaderInterceptor headerInterceptor;

    public WebConfig(HeaderInterceptor headerInterceptor, ServletContext servletContext) {
        this.headerInterceptor = headerInterceptor;
        // JSTL fmt:formatDate does not understand java.time, so JSPs use these with ${date.format(dFmt)}
        servletContext.setAttribute("dFmt", DateTimeFormatter.ofPattern("dd MMM yyyy"));
        servletContext.setAttribute("dtFmt", DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerInterceptor)
                .excludePathPatterns("/api/**", "/css/**", "/js/**");
    }
}
