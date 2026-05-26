package com.example.challengeservice.controllers;

import com.example.challengeservice.dtos.ChallengeResponseDto;
import com.example.challengeservice.entities.Challenge;
import com.example.challengeservice.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public List<ChallengeResponseDto> getAllChallenges() {
        return challengeService.getAllChallenges().stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateChallenges() {
        try {
            List<ChallengeResponseDto> challenges = challengeService.generateChallenges().stream()
                    .map(this::toDto)
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(challenges);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message",
                            "Could not reach Food Service. Make sure Service 1 is running on port 8080."));
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeChallenge(@PathVariable Long id) {
        return challengeService.completeChallenge(id)
                .<ResponseEntity<?>>map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Challenge with id " + id + " not found.")));
    }

    private ChallengeResponseDto toDto(Challenge c) {
        return ChallengeResponseDto.builder()
                .id(c.getId())
                .type(c.getType())
                .title(c.getTitle())
                .description(c.getDescription())
                .completed(c.isCompleted())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
