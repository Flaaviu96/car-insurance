package com.example.carins.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class InsuranceClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insurance_claim_seq")
    @SequenceGenerator(name = "insurance_claim_seq", sequenceName = "insurance_claim_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Car car;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate claimDate;

    private String description;

    @Positive
    private float amount;

    public InsuranceClaim() {
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
