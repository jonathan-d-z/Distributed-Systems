package com.example.profileservice.service;

import com.example.profileservice.entities.ProfileUser;
import com.example.profileservice.repositories.ProfileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private final ProfileUserRepository profileUserRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public void register(String username, String password) {
        String cleanedUsername = username != null ? username.trim() : "";

        if (cleanedUsername.length() < 3) {
            throw new IllegalArgumentException("Username must contain at least 3 characters.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (profileUserRepository.findByUsernameIgnoreCase(cleanedUsername).isPresent()) {
            throw new IllegalStateException("Username is already taken.");
        }

        profileUserRepository.save(ProfileUser.builder()
                .username(cleanedUsername)
                .passwordHash(hashPassword(password))
                .build());
    }

    public Optional<String> login(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        return profileUserRepository.findByUsernameIgnoreCase(username.trim())
                .filter(user -> verifyPassword(password, user.getPasswordHash()))
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    sessions.put(token, user.getUsername());
                    return token;
                });
    }

    public boolean isAuthorized(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authorizationHeader.substring("Bearer ".length());
        return sessions.containsKey(token);
    }

    private String hashPassword(String password) {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        byte[] actualHash = pbkdf2(password, salt);

        if (actualHash.length != expectedHash.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < actualHash.length; i++) {
            result |= actualHash[i] ^ expectedHash[i];
        }
        return result == 0;
    }

    private byte[] pbkdf2(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Could not hash password.", e);
        }
    }
}
