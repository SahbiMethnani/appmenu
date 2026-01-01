package com.example.appmenu.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Bean
    public Filter rateLimitFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws java.io.IOException, jakarta.servlet.ServletException {

                String ip = request.getRemoteAddr();
                Bucket bucket = cache.computeIfAbsent(ip, k -> Bucket.builder()
                        .addLimit(Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1))))
                        .build());

                if (bucket.tryConsume(1)) {
                    chain.doFilter(request, response);
                } else {
                    ((HttpServletResponse) response).sendError(429, "Too Many Requests");
                }
            }
        };
    }
}