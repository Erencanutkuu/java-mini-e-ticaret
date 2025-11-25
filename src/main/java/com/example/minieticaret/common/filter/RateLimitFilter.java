package com.example.minieticaret.common.filter;

import com.example.minieticaret.common.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final int limitPerMinute;
    private final Map<String, Window> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties properties) {
        this.limitPerMinute = properties.limitPerMinute();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = request.getRemoteAddr();
        if (isAllowed(key)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
        }
    }

    private boolean isAllowed(String key) {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - (now % 60);
        Window window = buckets.computeIfAbsent(key, k -> new Window(windowStart));
        synchronized (window) {
            if (window.start != windowStart) {
                window.start = windowStart;
                window.counter.set(0);
            }
            if (window.counter.incrementAndGet() > limitPerMinute) {
                return false;
            }
        }
        return true;
    }

    private static class Window {
        long start;
        AtomicInteger counter = new AtomicInteger(0);

        Window(long start) {
            this.start = start;
        }
    }
}
