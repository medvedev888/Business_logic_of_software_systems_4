package me.ifmo.backend.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterUserResponse {
    private Long id;
    private String email;
    private String role;
    private String camundaUserId;
}