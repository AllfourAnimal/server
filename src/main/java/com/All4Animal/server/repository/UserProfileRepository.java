package com.All4Animal.server.repository;

import com.All4Animal.server.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUsersLoginId(String loginId);
}
