package me.ifmo.backend.services;

import me.ifmo.backend.DTO.user.RegisterUserRequest;
import me.ifmo.backend.DTO.user.RegisterUserResponse;

public interface UserService {
    RegisterUserResponse register(RegisterUserRequest request);
}