package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByLoginId(String loginId);

    Optional<Users> findByLoginId(String loginId);
}
