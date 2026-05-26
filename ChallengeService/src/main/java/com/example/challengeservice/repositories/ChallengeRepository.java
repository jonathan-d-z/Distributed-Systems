package com.example.challengeservice.repositories;

import com.example.challengeservice.entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findAllByCompletedFalse();
    void deleteAllByCompletedFalse();
}
