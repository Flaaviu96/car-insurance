package com.example.carins.service.serviceImpl;

import com.example.carins.Exceptions.ApiException;
import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDTO;
import com.example.carins.web.dto.CarEventDTO;
import com.example.carins.web.dto.InsuranceClaimDTO;
import com.example.carins.web.dto.InsuranceValidityResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarServiceImpl(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<CarDTO> listCars() {
        List<Car> carList = carRepository.findAll();
        return carList.stream()
                .map(this::toDto)
                .toList();
    }

    public InsuranceValidityResponse isInsuranceValid(Long carId, String date) {
        if (carId == null || date == null) {
            throw new ApiException("Invalid format of date", HttpStatus.BAD_REQUEST);
        }
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
            boolean valid = policyRepository.existsActiveOnDate(carId, localDate);
            return new InsuranceValidityResponse(carId, localDate.toString(), valid);
        } catch (DateTimeParseException e) {
            throw new ApiException("Invalid format of date", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public boolean isInsuranceValid(Long carId, LocalDate localDate) {
        return false;
    }

    public InsuranceValidityResponse ceva(String date, Long carId) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
            if (carId == null) {
                throw new ApiException("Car ID cannot be null", HttpStatus.NOT_FOUND);
            }
            boolean valid = policyRepository.existsActiveOnDate(carId, localDate);
            return  new InsuranceValidityResponse(carId, localDate.toString(), valid);
        } catch (DateTimeParseException e) {
            throw new ApiException("Invalid format of date", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public InsuranceClaimDTO registerInsuranceClaim(Long carId, InsuranceClaimDTO insuranceClaimDTO) {
        if (carId == null) {
            throw  new ApiException("Car ID cannot be null", HttpStatus.NOT_FOUND);
        }
        InsuranceClaim insuranceClaim = fromDTO(insuranceClaimDTO);
        Car car = carRepository.findById(carId).orElseThrow(() -> new ApiException("Not found", HttpStatus.NOT_FOUND));
        car.addInsuranceClaim(insuranceClaim);
        insuranceClaim.setCar(car);
        carRepository.save(car);

        return toDTO(insuranceClaim);
    }

    @Override
    public List<CarEventDTO> getCarHistory(Long carId) {
        if (carId == null) {
            throw  new ApiException("Car ID cannot be null", HttpStatus.NOT_FOUND);
        }

        Car car = carRepository.findById(carId).orElseThrow(() -> new ApiException("Not found", HttpStatus.NOT_FOUND));

        return car.getInsuranceClaimSet().stream()
                .map(this::insuranceClaimToEventDTO)
                .sorted(Comparator.comparing(CarEventDTO::date).reversed())
                .toList();
    }

    private InsuranceClaim fromDTO(InsuranceClaimDTO insuranceClaimDTO) {
        return new InsuranceClaim(insuranceClaimDTO.claimDate(), insuranceClaimDTO.description(), insuranceClaimDTO.amount());
    }

    private CarEventDTO insuranceClaimToEventDTO(InsuranceClaim insuranceClaim) {
        return new CarEventDTO(insuranceClaim.getClaimDate(), insuranceClaim.getDescription(), insuranceClaim.getAmount());
    }

    private InsuranceClaimDTO toDTO(InsuranceClaim insuranceClaim) {
        return new InsuranceClaimDTO(insuranceClaim.getId(), insuranceClaim.getClaimDate(), insuranceClaim.getDescription(), insuranceClaim.getAmount());
    }

    private CarDTO toDto(Car c) {
        var o = c.getOwner();
        return new CarDTO(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }
}
