package com.example.api_gateaway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {


    @GetMapping("/gym-service")
    public ResponseEntity<Map<String, Object>> gymServiceFallback() {
        log.warn("Gym Service is unavailable. Returning fallback response.");
        return createFallbackResponse("Gym Service");
    }

    @GetMapping("/workload-service")
    public ResponseEntity<Map<String, Object>> workloadServiceFallback() {
        log.warn("Training Workload Service is unavailable. Returning fallback response.");
        return createFallbackResponse("Training Workload Service");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", serviceName + " is temporarily unavailable. Please try again later.");
        response.put("service", serviceName);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

