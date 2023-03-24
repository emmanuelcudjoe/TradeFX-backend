package com.cjvisions.tradefx_backend.domain.dto;

public record UserRegistrationDetails(
        String firstName,
        String lastName,
        String email,
        String password
        ) {
}
