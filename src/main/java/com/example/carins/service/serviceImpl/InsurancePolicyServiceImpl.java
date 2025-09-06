package com.example.carins.service.serviceImpl;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.InsurancePolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    private final InsurancePolicyRepository insurancePolicyRepository;

    public InsurancePolicyServiceImpl(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    @Override
    public List<InsurancePolicy> listInsuranceWithoutEndDate() {
        return insurancePolicyRepository.findByEndDateIsNull();
    }
}
