package com.example.carins.web;

import com.example.carins.service.CarService;
import com.example.carins.service.serviceImpl.CarServiceImpl;
import com.example.carins.web.dto.CarDTO;
import com.example.carins.web.dto.CarEventDTO;
import com.example.carins.web.dto.InsuranceClaimDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getCars() {
        return ResponseEntity.ok(service.listCars());
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        return ResponseEntity.ok(service.isInsuranceValid(carId, date));
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<List<CarEventDTO>> getCarHistory(@PathVariable long carId) {
        return ResponseEntity.ok(service.getCarHistory(carId));
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<?> registerInsuranceClaim(@PathVariable long carId, @RequestBody @Valid InsuranceClaimDTO insuranceClaimDTO) {
        InsuranceClaimDTO insuranceClaim = service.registerInsuranceClaim(carId, insuranceClaimDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(insuranceClaim.id())
                .toUri();

        return ResponseEntity.created(location).body(insuranceClaim);
    }
}
