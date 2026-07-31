package com.example.challengeservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChallengeResponseDto {
    private Long id;
    private String type;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}
