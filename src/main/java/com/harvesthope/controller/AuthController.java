package com.harvesthope.controller;

import com.harvesthope.model.dto.LoginRequest;
import com.harvesthope.model.dto.LoginResponse;
import com.harvesthope.model.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {

        LoginResponse response = authService.autenticar(
                loginRequest.getEmail(),
                loginRequest.getSenha()
        );

        if (response == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(response);
    }
}