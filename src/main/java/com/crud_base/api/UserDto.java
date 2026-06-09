package com.crud_base.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,
        @Size(min = 3, max = 20)
        String username,
        @Size(min = 3, max = 20)
        String surname,
        @Email(message = "Email должен быть корректным")
        @Size(max = 255, message = "Email не может быть длиннее 255 символов")
        String email,
        @Positive
        @Max(100)
        Integer age
) {
}
