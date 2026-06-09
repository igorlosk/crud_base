package com.crud_base.web;

import java.time.LocalDateTime;

public record ServerErrorDto(
        String message,
        String detailMessage,
        LocalDateTime dateTime
) {
}
