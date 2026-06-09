package com.crud_base.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record UserDto(
        Long id,
        String username,
        String surname,
        @Email
        String email,
        @Positive
        @Max(100)
        Integer age
) {
}
