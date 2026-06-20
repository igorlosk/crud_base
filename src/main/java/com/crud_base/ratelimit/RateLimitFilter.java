package com.crud_base.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final FixedWindowRateLimiter fixedWindowRateLimiter;

    public RateLimitFilter(FixedWindowRateLimiter fixedWindowRateLimiter) {
        this.fixedWindowRateLimiter = fixedWindowRateLimiter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String client = Optional.ofNullable(request.getHeader("X-API-KEY"))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> Optional.ofNullable(request.getRemoteAddr()).orElse("unknown"));

        boolean allowed = fixedWindowRateLimiter.allowRequest(
                client,
                10,
                Duration.ofMinutes(1)
        );

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
