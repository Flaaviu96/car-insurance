package com.example.carins;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.InsurancePolicyService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class CarInsuranceApplication implements CommandLineRunner {

    private final InsurancePolicyService insurancePolicyService;

    public CarInsuranceApplication(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarInsuranceApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<InsurancePolicy> insurancePolicyList = insurancePolicyService.listInsuranceWithoutEndDate();
        if (insurancePolicyList != null && !insurancePolicyList.isEmpty()) {
            for (InsurancePolicy insurancePolicy : insurancePolicyList) {
                LocalDate endDate = insurancePolicy.getStartDate().plusYears(1);
                insurancePolicy.setEndDate(endDate);
            }
        }
    }
}