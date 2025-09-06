package com.example.carins.Mapper;

import com.example.carins.Exceptions.ApiException;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.web.dto.InsuranceClaimRequestDTO;
import com.example.carins.web.dto.InsuranceClaimResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class InsuranceClaimMapper {

    private InsuranceClaimMapper() {}

    public static InsuranceClaim toEntity(InsuranceClaimRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new ApiException("Insurance claim cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (requestDTO.claimDate() == null) {
            throw new ApiException("Claim date is required", HttpStatus.BAD_REQUEST);
        }
        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            throw new ApiException("Description cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (requestDTO.amount() <= 0) {
            throw new ApiException("Amount must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        InsuranceClaim claim = new InsuranceClaim();
        claim.setClaimDate(requestDTO.claimDate());
        claim.setDescription(requestDTO.description());
        claim.setAmount(requestDTO.amount());
        return claim;
    }

    public static InsuranceClaimResponseDTO toResponseDTO(InsuranceClaim claim) {
        if (claim == null) {
            throw new ApiException("Insurance claim cannot be null", HttpStatus.BAD_REQUEST);
        }

        return new InsuranceClaimResponseDTO(
                claim.getId(),
                claim.getClaimDate(),
                claim.getDescription(),
                claim.getAmount()
        );
    }
}
