package com.example.profileservice.repositories;

import com.example.profileservice.entities.ProfileUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileUserRepository extends JpaRepository<ProfileUser, Long> {
    Optional<ProfileUser> findByKeycloakSubject(String keycloakSubject);
}
