package com.example.demo.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class WebConfig {

    @Bean
    public Filter swaggerContentTypeFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;

                String uri = httpRequest.getRequestURI();
                if (uri.endsWith("/swagger-ui/index.html")) {
                    // Force proper content type for Swagger UI
                    httpResponse.setContentType("text/html;charset=UTF-8");
                }

                chain.doFilter(request, response);
            }
        };
    }
}
