package com.example.profileservice.service;

import com.example.profileservice.entities.ProfileUser;
import com.example.profileservice.repositories.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileUserRepository profileUserRepository;

    @Transactional
    public ProfileUser getOrCreateProfile(Jwt jwt) {
        String subject = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null || username.isBlank()) {
            username = subject;
        }

        String currentUsername = username;
        ProfileUser profile = profileUserRepository.findByKeycloakSubject(subject)
                .orElseGet(() -> ProfileUser.builder()
                        .keycloakSubject(subject)
                        .username(currentUsername)
                        .build());
        profile.setUsername(currentUsername);
        return profileUserRepository.save(profile);
    }
}
