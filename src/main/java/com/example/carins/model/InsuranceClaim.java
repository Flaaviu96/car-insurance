package com.example.carins.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class InsuranceClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Car car;

    @NotNull
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate claimDate;

    private String description;

    private float amount;

    public InsuranceClaim() {
    }

    public InsuranceClaim (LocalDate claimDate, String description, float amount) {
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }

    public long getId() { return id;}

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
