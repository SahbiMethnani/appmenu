package com.example.appmenu.dto;

import jakarta.validation.constraints.*;

public record AdminLoginRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8) String password
) {}