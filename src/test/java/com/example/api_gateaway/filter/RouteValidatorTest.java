package com.example.api_gateaway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.junit.jupiter.api.Assertions.*;

class RouteValidatorTest {

    private final RouteValidator routeValidator = new RouteValidator();

    @Test
    void isSecured_authLoginEndpoint_returnsFalse() {
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/login").build();
        assertFalse(routeValidator.isSecured.test(request));
    }

    @Test
    void isSecured_authRegisterEndpoint_returnsFalse() {
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/register").build();
        assertFalse(routeValidator.isSecured.test(request));
    }

    @Test
    void isSecured_actuatorHealthEndpoint_returnsFalse() {
        ServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        assertFalse(routeValidator.isSecured.test(request));
    }

    @Test
    void isSecured_fallbackEndpoint_returnsFalse() {
        ServerHttpRequest request = MockServerHttpRequest.get("/fallback/gym-service").build();
        assertFalse(routeValidator.isSecured.test(request));
    }

    @Test
    void isSecured_securedEndpoint_returnsTrue() {
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/trainers").build();
        assertTrue(routeValidator.isSecured.test(request));
    }

    @Test
    void isSecured_workloadEndpoint_returnsTrue() {
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/workload/trainer1").build();
        assertTrue(routeValidator.isSecured.test(request));
    }
}

