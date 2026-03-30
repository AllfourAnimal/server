package com.All4Animal.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long userId;

    private String username;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    private String password;

    @OneToOne(mappedBy = "users")
    private UserProfile userProfile;

}
