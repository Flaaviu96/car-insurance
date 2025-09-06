package com.example.carins.web.dto;

import java.time.LocalDate;

public record InsuranceClaimResponseDTO(
        Long id,
        LocalDate claimDate,
        String description,
        float amount
) {}
