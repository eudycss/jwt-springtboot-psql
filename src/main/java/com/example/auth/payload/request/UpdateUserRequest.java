package com.example.auth.payload.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 20)
    private String username;

    @Size(min = 8, max = 100)
    private String password;

    private Set<String> roles;
} 