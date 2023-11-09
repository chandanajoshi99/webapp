package com.csye6225.Assignment3.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component
@Order(1)
public class LogConfig extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(LogConfig.class);
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Log the request method, URL, and headers
        logger.info("API Request: {} {} from {} with headers: {}", request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr(), request.getHeader("User-Agent"));
        // Continue with the filter chain to process the request
        filterChain.doFilter(request, response);
        // Log the response status code and headers
        logger.info("API Response: Status Code: {} with headers: {}", response.getStatus(), response.getHeaderNames());
    }
}
