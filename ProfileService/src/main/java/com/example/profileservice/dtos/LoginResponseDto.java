package com.example.profileservice.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String username;
    private String token;
}
