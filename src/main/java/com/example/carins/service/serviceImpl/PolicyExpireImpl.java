package com.example.carins.service.serviceImpl;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.PolicyExpire;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PolicyExpireImpl implements PolicyExpire {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final Clock clock;

    public PolicyExpireImpl(InsurancePolicyRepository insurancePolicyRepository, Clock clock) {
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.clock = clock;
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void logExpiredPolicies() {
        LocalDate today = LocalDate.now(clock);
        List<InsurancePolicy> insurancePolicyList =
                insurancePolicyRepository.findByEndDateAndIsExpiredFalse(today);

        LocalDateTime now = LocalDateTime.now(clock);

        for (InsurancePolicy insurancePolicy : insurancePolicyList) {
            LocalDateTime expiredAt = insurancePolicy.getEndDate().atStartOfDay();

            if (now.isAfter(expiredAt) && now.isBefore(expiredAt.plusHours(1))) {
                insurancePolicy.setExpired(true);
                insurancePolicyRepository.save(insurancePolicy);
            }
        }
    }
}
