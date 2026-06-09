package com.crud_base.domain;

public record User(
        Long id,
        String username,
        String surname,
        String email,
        Integer age
) {
}
