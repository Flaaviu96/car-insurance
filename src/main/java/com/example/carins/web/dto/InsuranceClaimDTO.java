package com.example.carins.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InsuranceClaimDTO(
        long id,

        @NotNull(message = "Claim date is required")
        LocalDate claimDate,

        @NotEmpty(message = "Description cannot be empty")
        String description,

        @Positive(message = "Amount must be greater than 0")
        float amount
) {}
