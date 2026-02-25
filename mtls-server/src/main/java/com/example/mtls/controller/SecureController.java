package com.example.mtls.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SecureController {

    @GetMapping("/secure")
    public Map<String, String> secureEndpoint(Principal principal) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully accessed secure mTLS endpoint!");
        response.put("clientName", principal != null ? principal.getName() : "Unknown");
        return response;
    }
}
