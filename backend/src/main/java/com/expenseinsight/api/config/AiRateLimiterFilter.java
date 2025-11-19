package com.expenseinsight.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(200)
public class AiRateLimiterFilter extends OncePerRequestFilter {

    @Value("${ai.rate.limit.requests-per-minute:60}")
    private long requestsPerMinute;

    @Value("${ai.rate.limit.capacity:60}")
    private long capacity;

    @Value("${ai.rate.limit.premium.requests-per-minute:200}")
    private long premiumRequestsPerMinute;

    @Value("${ai.rate.limit.free.requests-per-minute:10}")
    private long freeRequestsPerMinute;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private final Map<String, SlidingWindow> clients = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.startsWith("/api/ai/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = resolveKey(request);

        long limit = determineLimitForRequest(request);

        boolean allowed;
        long remaining = 0;
        long resetSeconds = 60;

        if (redisTemplate != null) {
            // Use fixed-window counter in Redis keyed by minute
            long window = Instant.now().getEpochSecond() / 60;
            String redisKey = "rate:" + key + ":" + window;
            Long current = redisTemplate.opsForValue().increment(redisKey);
            if (current != null && current == 1L) {
                // first time: set TTL slightly longer than window
                redisTemplate.expireAt(redisKey, Instant.ofEpochSecond((window + 1) * 60));
            }
            long curr = current == null ? 0L : current.longValue();
            allowed = curr <= limit;
            remaining = Math.max(0L, limit - curr);
            resetSeconds = (window + 1) * 60 - Instant.now().getEpochSecond();
        } else {
            SlidingWindow window = clients.computeIfAbsent(key, k -> new SlidingWindow(requestsPerMinute, Duration.ofMinutes(1)));
            allowed = window.tryConsume();
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetSeconds));

        if (allowed) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(resetSeconds));
            response.getWriter().write("Too many requests - rate limit exceeded for AI endpoints");
        }
    }


    private static class SlidingWindow {
        private final long capacity;
        private final Duration windowSize;
        private Instant windowStart;
        private AtomicInteger count;

        SlidingWindow(long capacity, Duration windowSize) {
            this.capacity = capacity;
            this.windowSize = windowSize;
            this.windowStart = Instant.now();
            this.count = new AtomicInteger(0);
        }

        synchronized boolean tryConsume() {
            Instant now = Instant.now();
            if (Duration.between(windowStart, now).compareTo(windowSize) >= 0) {
                windowStart = now;
                count.set(0);
            }
            if (count.get() < capacity) {
                count.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return "user:" + auth.getName();
        }
        String ip = request.getRemoteAddr();
        return "ip:" + (ip == null ? "unknown" : ip);
    }

    private long determineLimitForRequest(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // check roles for premium
            Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.toSet());
            if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_PREMIUM")) {
                return premiumRequestsPerMinute;
            }
            // default authenticated
            return requestsPerMinute;
        }
        // unauthenticated users are treated as free tier
        return freeRequestsPerMinute;
    }
}
