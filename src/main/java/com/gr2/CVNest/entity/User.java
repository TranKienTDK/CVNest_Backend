package com.gr2.CVNest.entity;

import com.gr2.CVNest.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String password;
    private String fullName;
    private int age;
    private GenderEnum gender;
    private String phone;
    private String address;
    private String refreshToken;
    private boolean isActivated;
    private Instant createdAt;
    private Instant updatedAt;
}
