package com.example.carins;

import com.example.carins.Exceptions.ApiException;
import com.example.carins.model.Car;
import com.example.carins.service.serviceImpl.CarServiceImpl;
import com.example.carins.web.dto.CarDTO;
import com.example.carins.web.dto.CarEventDTO;
import com.example.carins.web.dto.InsuranceClaimRequestDTO;
import com.example.carins.web.dto.InsuranceClaimResponseDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CarInsuranceApplicationTests {

    @Autowired
    CarServiceImpl service;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void testIsInsuranceValidInvalidDate() {
        Long carId = 1L;
        String invalidDate = "2025-02-30";

        ApiException exception = null;

        try {
            service.isInsuranceValid(carId, invalidDate);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Invalid or impossible date, expected yyyy-MM-dd", exception.getMessage());


        carId = null;
        String validDate = "2025-02-23";

        try{
            service.isInsuranceValid(carId, validDate);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Car ID and date must not be null", exception.getMessage());
    }

    @Test
    void testRegisterInsuranceClaimCarNotFound() {
        ApiException apiException = null;
        Long carId = null;
        InsuranceClaimRequestDTO requestClaim = createRequest(
                LocalDate.of(2025, 10, 1),
                "Custom claim",
                2500F
        );
        try {
            service.registerInsuranceClaim(carId, requestClaim);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            apiException = e;
        }

        assertNotNull(apiException);
        assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatus());
        assertEquals("Car ID cannot be null", apiException.getMessage());

        carId = 999L;
        try {
            service.registerInsuranceClaim(carId, requestClaim);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            apiException = e;
        }

        assertNotNull(apiException);
        assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatus());
        assertEquals("Car not found", apiException.getMessage());


        carId = 1L;
        try {
            service.registerInsuranceClaim(carId, null);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            apiException = e;
        }

        assertNotNull(apiException);
        assertEquals(HttpStatus.BAD_REQUEST, apiException.getHttpStatus());
        assertEquals("Insurance claim cannot be null", apiException.getMessage());
    }

    @Test
    void testRegisterInsurance() {
        Long carId = 1L;
        InsuranceClaimRequestDTO requestClaim = createRequest(
                LocalDate.of(2025, 10, 1),
                "Custom claim",
                2500F
        );
        InsuranceClaimResponseDTO responseDTO = service.registerInsuranceClaim(carId, requestClaim);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.id());
        assertEquals(requestClaim.claimDate(), responseDTO.claimDate());
        assertEquals(requestClaim.description(), responseDTO.description());
        assertEquals(requestClaim.amount(), responseDTO.amount());

    }

    @Test
    void testCarHistoryInvalidCarId() {
        Long carId = null;
        ApiException apiException = null;
        try {
            service.getCarHistory(carId);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            apiException = e;
        }

        assertNotNull(apiException);
        assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatus());
        assertEquals("Car ID cannot be null", apiException.getMessage());
    }

    @Test
    void testCarHistoryCarNotFound() {
        Long carId = 999L;
        ApiException apiException = null;
        try {
            service.getCarHistory(carId);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            apiException = e;
        }

        assertNotNull(apiException);
        assertEquals(HttpStatus.NOT_FOUND, apiException.getHttpStatus());
        assertEquals("Car not found", apiException.getMessage());
    }

    @Test
    void testCarHistory() {
        Long carId = 1L;

        InsuranceClaimRequestDTO firstClaim = createRequest(
                LocalDate.of(2024, 9, 1),
                "Custom claim",
                2500F
        );

        InsuranceClaimRequestDTO secondClaim = createRequest(
                LocalDate.of(2023, 1, 1),
                "Custom claim",
                2500F
        );

        InsuranceClaimRequestDTO thirdClaim = createRequest(
                LocalDate.of(2025, 4, 1),
                "Custom claim",
                2500F
        );
        service.registerInsuranceClaim(carId, firstClaim);
        service.registerInsuranceClaim(carId, secondClaim);
        service.registerInsuranceClaim(carId, thirdClaim);

        List<CarEventDTO> list = service.getCarHistory(carId);


        assertEquals(3, list.size());
        assertEquals(LocalDate.of(2025, 4, 1), list.get(0).date());
        assertEquals(LocalDate.of(2024, 9, 1), list.get(1).date());
        assertEquals(LocalDate.of(2023, 1, 1), list.get(2).date());
    }

    private InsuranceClaimRequestDTO createRequest(LocalDate claimDate, String description, float amount) {
        return new InsuranceClaimRequestDTO(claimDate, description, amount);
    }
}
