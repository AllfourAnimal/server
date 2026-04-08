package com.All4Animal.server.repository;

import com.All4Animal.server.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    // 나중에 유저랑 유저 preference를 같이 가져올거면 fetch join
}
