package com.example.carins;

import com.example.carins.GlobalExceptionHandler.GlobalExceptionHandler;
import com.example.carins.web.CarController;
import com.example.carins.web.dto.CarEventDTO;
import com.example.carins.web.dto.InsuranceClaimRequestDTO;
import com.example.carins.web.dto.InsuranceClaimResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@Import(GlobalExceptionHandler.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private com.example.carins.service.CarService service;

    @Test
    void testInvalidInsuranceClaimAllFieldsInvalid() throws Exception {
        InsuranceClaimRequestDTO dto = new InsuranceClaimRequestDTO(
                null,
                "",
                -100f
        );

        mockMvc.perform(post("/api/cars/1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Claim date is required")))
                .andExpect(content().string(containsString("Description cannot be empty")))
                .andExpect(content().string(containsString("Amount must be greater than 0")));
    }

    @Test
    void testRegisterInsuranceClaim() throws Exception {
        InsuranceClaimRequestDTO dto = new InsuranceClaimRequestDTO(
                LocalDate.of(2025, 4, 1),
                "Custom claim",
                2500F
        );

        InsuranceClaimResponseDTO responseDTO = new InsuranceClaimResponseDTO(
                1L,
                LocalDate.of(2025, 4, 1),
                "Custom claim",
                2500F
        );

        when(service.registerInsuranceClaim(eq(1L), eq(dto))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cars/1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/cars/1/claims/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.claimDate").value("2025-04-01"))
                .andExpect(jsonPath("$.description").value("Custom claim"))
                .andExpect(jsonPath("$.amount").value(2500.0));

        verify(service).registerInsuranceClaim(eq(1L), eq(dto));
    }

    @Test
    void testCarHistory() throws Exception {

        List<CarEventDTO> history = List.of(
                new CarEventDTO( LocalDate.of(2025, 4, 1),"Claim", 1200),
                new CarEventDTO(LocalDate.of(2025, 5, 10),"Claim", 3000)
        );

        when(service.getCarHistory(1L)).thenReturn(history);

        mockMvc.perform(get("/api/cars/1/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(history.size()));

        verify(service).getCarHistory(eq(1L));
    }
}
