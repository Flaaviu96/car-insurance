package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;

import java.util.List;

public interface InsurancePolicyService {
    List<InsurancePolicy> listInsuranceWithoutEndDate();
}
