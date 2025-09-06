package com.example.carins.service;

import com.example.carins.web.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CarService {

    List<CarDTO> listCars();

    InsuranceValidityResponse isInsuranceValid(Long carId, String date);

    boolean isInsuranceValid(Long carId, LocalDate localDate);

    InsuranceClaimResponseDTO registerInsuranceClaim(Long carId, InsuranceClaimRequestDTO insuranceClaimDTO);

    List<CarEventDTO> getCarHistory(Long carId);
}
