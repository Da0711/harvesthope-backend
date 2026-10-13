package com.harvesthope.controller;

import com.harvesthope.model.dto.LoginRequest;
import com.harvesthope.model.dto.LoginResponse;
import com.harvesthope.model.service.AuthService;

import org.springframework.http.HttpStatus;
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
            @RequestBody(required = false) LoginRequest loginRequest) {

        if (loginRequest == null) {
            return ResponseEntity.badRequest().build();
        }

        LoginResponse response = authService.autenticar(
                loginRequest.getEmail(),
                loginRequest.getSenha()
        );

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(response);
    }
}