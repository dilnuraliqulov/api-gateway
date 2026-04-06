package com.example.api_gateaway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String transactionId = request.getHeaders().getFirst("X-Transaction-Id");
        if (transactionId == null) {
            transactionId = "N/A";
        }

        // Log request details
        log.info("================== Gateway Request ==================");
        log.info("Transaction ID: {}", transactionId);
        log.info("Request Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getURI());
        log.info("Request Path: {}", request.getPath());
        log.info("Request Headers: {}", request.getHeaders());
        log.info("======================================================");

        long startTime = System.currentTimeMillis();
        String finalTransactionId = transactionId;

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            // Log response details
            log.info("================== Gateway Response ==================");
            log.info("Transaction ID: {}", finalTransactionId);
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());
            log.info("Request Duration: {} ms", duration);
            log.info("=======================================================");
        }));
    }

    @Override
    public int getOrder() {
        return -2; // Higher priority than AuthenticationFilter
    }
}

