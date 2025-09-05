package com.example.carins.web.dto;

import java.time.LocalDate;

public record CarEventDTO(LocalDate date, String description, float amount) {
}
