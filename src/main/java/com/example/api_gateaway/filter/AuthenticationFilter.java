package com.example.api_gateaway.filter;

import com.example.api_gateaway.config.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    public AuthenticationFilter(JwtUtil jwtUtil, RouteValidator routeValidator) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate transaction ID for logging
        String transactionId = UUID.randomUUID().toString();

        // Add transaction ID to MDC for logging
        log.info("Transaction ID: {} - Incoming request: {} {}",
                transactionId, request.getMethod(), request.getURI().getPath());

        // Add transaction ID to request headers for downstream services
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Transaction-Id", transactionId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // Check if the route requires authentication
        if (routeValidator.isSecured.test(request)) {
            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Transaction ID: {} - Missing Authorization header", transactionId);
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED, transactionId);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Transaction ID: {} - Invalid Authorization header format", transactionId);
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED, transactionId);
            }

            String token = authHeader.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Transaction ID: {} - Invalid or expired JWT token", transactionId);
                    return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED, transactionId);
                }

                String username = jwtUtil.extractUsername(token);
                log.info("Transaction ID: {} - Authenticated user: {}", transactionId, username);

                // Add username to request header for downstream services
                mutatedRequest = mutatedRequest.mutate()
                        .header("X-Authenticated-User", username)
                        .build();

                mutatedExchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

            } catch (Exception e) {
                log.error("Transaction ID: {} - JWT validation error: {}", transactionId, e.getMessage());
                return onError(exchange, "JWT validation error: " + e.getMessage(), HttpStatus.UNAUTHORIZED, transactionId);
            }
        }

        log.debug("Transaction ID: {} - Routing request to downstream service", transactionId);

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            log.info("Transaction ID: {} - Response status: {}", transactionId, response.getStatusCode());
        }));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus, String transactionId) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("X-Transaction-Id", transactionId);
        log.error("Transaction ID: {} - Error response: {} - {}", transactionId, httpStatus, message);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}

