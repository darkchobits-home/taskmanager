package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.security.AuthRequest;
import com.sebastien.taskmanager.security.AuthResponse;
import com.sebastien.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        System.out.println(">>> LOGIN called with username = " + authRequest.getUsername());

        final AuthResponse authResponse = authService.login(authRequest);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
