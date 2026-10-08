package com.rant.config;

import com.rant.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Get identifier (prefer X-Client-Hash, fallback to IP)
        String clientHash = request.getHeader("X-Client-Hash");
        String ip = request.getRemoteAddr();
        String identifier = (clientHash != null && !clientHash.isEmpty()) ? clientHash : ip;

        Bucket bucket = null;

        if (uri.equals("/api/rants") && method.equals("POST")) {
            bucket = rateLimiterService.resolvePostBucket(identifier);
        } else if (uri.startsWith("/api/rants/") && uri.endsWith("/reaction")) {
            bucket = rateLimiterService.resolveReactionBucket(identifier);
        }

        if (bucket != null) {
            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                if (uri.equals("/api/rants") && method.equals("POST")) {
                    response.getWriter().write("Only one rant allowed per 24 hours.");
                } else {
                    response.getWriter().write("Too many requests. Please try again later.");
                }
                return false;
            }
        }

        return true;
    }
}
