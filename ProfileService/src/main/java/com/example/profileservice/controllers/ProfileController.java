package com.example.profileservice.controllers;

import com.example.profileservice.dtos.LoginRequestDto;
import com.example.profileservice.dtos.LoginResponseDto;
import com.example.profileservice.dtos.RegisterRequestDto;
import com.example.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        return profileService.login(request.getUsername(), request.getPassword())
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(LoginResponseDto.builder()
                        .username(request.getUsername())
                        .token(token)
                        .build()))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password.")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        try {
            profileService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Account created."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return profileService.isAuthorized(authorizationHeader)
                ? ResponseEntity.ok(Map.of("valid", true))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
    }
}
