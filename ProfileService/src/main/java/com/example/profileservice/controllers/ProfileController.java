package com.example.profileservice.controllers;

import com.example.profileservice.entities.ProfileUser;
import com.example.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        ProfileUser profile = profileService.getOrCreateProfile(jwt);
        return ResponseEntity.ok(Map.of(
                "id", profile.getId(),
                "subject", profile.getKeycloakSubject(),
                "username", profile.getUsername(),
                "createdAt", profile.getCreatedAt()));
    }
}
