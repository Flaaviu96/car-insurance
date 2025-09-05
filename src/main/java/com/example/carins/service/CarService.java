package com.example.carins.service;

import com.example.carins.web.dto.CarDTO;
import com.example.carins.web.dto.CarEventDTO;
import com.example.carins.web.dto.InsuranceClaimDTO;
import com.example.carins.web.dto.InsuranceValidityResponse;

import java.time.LocalDate;
import java.util.List;

public interface CarService {

    List<CarDTO> listCars();

    InsuranceValidityResponse isInsuranceValid(Long carId, String date);

    boolean isInsuranceValid(Long carId, LocalDate localDate);

    InsuranceClaimDTO registerInsuranceClaim(Long carId, InsuranceClaimDTO insuranceClaimDTO);

    List<CarEventDTO> getCarHistory(Long carId);
}
