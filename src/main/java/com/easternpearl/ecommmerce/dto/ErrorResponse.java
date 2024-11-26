package com.easternpearl.ecommmerce.dto;

public record ErrorResponse(
        int status,
        String message,
        String details
) {
}
