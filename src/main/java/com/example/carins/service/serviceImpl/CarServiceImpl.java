package com.example.carins.service.serviceImpl;

import com.example.carins.Exceptions.ApiException;
import com.example.carins.Mapper.InsuranceClaimMapper;
import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;

    public CarServiceImpl(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository insuranceClaimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
    }

    public List<CarDTO> listCars() {
        List<Car> carList = carRepository.findAll();
        return carList.stream()
                .map(this::toDto)
                .toList();
    }

    public InsuranceValidityResponse isInsuranceValid(Long carId, String date) {
        if (carId == null || date == null) {
            throw new ApiException("Car ID and date must not be null", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate localDate = LocalDate.parse(date);
            boolean valid = isInsuranceValid(carId, localDate);
            return new InsuranceValidityResponse(carId, localDate.toString(), valid);
        } catch (DateTimeException e) {
            throw new ApiException("Invalid or impossible date, expected yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public boolean isInsuranceValid(Long carId, LocalDate localDate) {
        if (carId == null || localDate == null) {
            throw new ApiException("Car ID and date must not be null", HttpStatus.BAD_REQUEST);
        }

        return policyRepository.existsActiveOnDate(carId, localDate);
    }

    @Override
    @Transactional
    public InsuranceClaimResponseDTO registerInsuranceClaim(Long carId, InsuranceClaimRequestDTO insuranceClaimDTO) {
        if (carId == null) {
            throw  new ApiException("Car ID cannot be null", HttpStatus.NOT_FOUND);
        }
        InsuranceClaim insuranceClaim = InsuranceClaimMapper.toEntity(insuranceClaimDTO);
        Car car = carRepository.findById(carId).orElseThrow(() -> new ApiException("Car not found", HttpStatus.NOT_FOUND));
        car.addInsuranceClaim(insuranceClaim);
        insuranceClaim = insuranceClaimRepository.saveAndFlush(insuranceClaim);

        return InsuranceClaimMapper.toResponseDTO(insuranceClaim);
    }

    @Override
    public List<CarEventDTO> getCarHistory(Long carId) {
        if (carId == null) {
            throw  new ApiException("Car ID cannot be null", HttpStatus.NOT_FOUND);
        }

        Car car = carRepository.findByIdWithClaims(carId).orElseThrow(() -> new ApiException("Car not found", HttpStatus.NOT_FOUND));

        return car.getInsuranceClaimSet().stream()
                .map(this::insuranceClaimToEventDTO)
                .sorted(Comparator.comparing(CarEventDTO::date).reversed())
                .toList();
    }

    private CarEventDTO insuranceClaimToEventDTO(InsuranceClaim insuranceClaim) {
        return new CarEventDTO(insuranceClaim.getClaimDate(), insuranceClaim.getDescription(), insuranceClaim.getAmount());
    }

    private CarDTO toDto(Car c) {
        var o = c.getOwner();
        return new CarDTO(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }
}
