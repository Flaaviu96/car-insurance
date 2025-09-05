package com.example.carins.service.serviceImpl;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PolicyExpireImpl {

    private static final Logger logger = LoggerFactory.getLogger(PolicyExpireImpl.class);
    private final InsurancePolicyRepository insurancePolicyRepository;

    public PolicyExpireImpl(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    @Scheduled(cron = "*/30 * * * *")
    @Transactional
    public void logExpiredPolicies() {
        LocalDate today = LocalDate.now();
        List<InsurancePolicy> insurancePolicyList = insurancePolicyRepository.findByEndDateAndIsExpiredFalse(today);

        LocalDateTime now = LocalDateTime.now();

        for (InsurancePolicy insurancePolicy : insurancePolicyList) {
            LocalDateTime expiredAt  = insurancePolicy.getEndDate().atStartOfDay();

            if (now.isAfter(expiredAt) && now.isBefore(expiredAt.plusHours(1))) {
                logger.info("Policy {} for car {} expired on {}",
                        insurancePolicy.getId(),
                        insurancePolicy.getCar().getId(),
                        insurancePolicy.getEndDate());

                insurancePolicy.setExpired(true);
                insurancePolicyRepository.save(insurancePolicy);
            }
        }
    }
}
