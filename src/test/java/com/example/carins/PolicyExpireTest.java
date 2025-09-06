package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.serviceImpl.PolicyExpireImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.*;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class PolicyExpireTest {
    private InsurancePolicyRepository insurancePolicyRepository;
    private PolicyExpireImpl policyExpireImpl;

    @BeforeEach
    void setUp() {
        insurancePolicyRepository = mock(InsurancePolicyRepository.class);

        Clock fixedClock = Clock.fixed(
                LocalDate.now().atTime(0, 30).toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())),
                ZoneId.systemDefault()
        );

        policyExpireImpl = new PolicyExpireImpl(insurancePolicyRepository, fixedClock);
    }

    @Test
    void shouldMarkPolicyAsExpiredWhenEndDateIsToday() {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setId(1L);
        policy.setEndDate(LocalDate.now());
        policy.setExpired(false);

        Car car = new Car();
        car.setId(101L);
        policy.setCar(car);

        when(insurancePolicyRepository.findByEndDateAndIsExpiredFalse(LocalDate.now()))
                .thenReturn(Collections.singletonList(policy));

        policyExpireImpl.logExpiredPolicies();

        ArgumentCaptor<InsurancePolicy> captor = ArgumentCaptor.forClass(InsurancePolicy.class);
        verify(insurancePolicyRepository, times(1)).save(captor.capture());

        InsurancePolicy savedPolicy = captor.getValue();
        assertThat(savedPolicy.isExpired()).isTrue();
    }

}
