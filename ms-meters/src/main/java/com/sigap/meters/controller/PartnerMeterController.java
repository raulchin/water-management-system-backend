package com.sigap.meters.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigap.meters.dto.ApiResponse;
import com.sigap.meters.dto.CreatePartnerMeterRequest;
import com.sigap.meters.dto.PartnerMeterResponse;
import com.sigap.meters.dto.UpdatePartnerMeterRequest;
import com.sigap.meters.service.PartnerMeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/medidor-socios")
@RequiredArgsConstructor
public class PartnerMeterController {

    private final PartnerMeterService partnerMeterService;

    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<PartnerMeterResponse>> create(
            @Valid @RequestBody CreatePartnerMeterRequest request
    ) {
        log.info("Incoming request to create partner meter assignment: {}", toJson(request));
        PartnerMeterResponse response = partnerMeterService.create(request);

        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Medidor asignado correctamente al socio", response));
    }

    @GetMapping("/{asignacionId}")
    public ResponseEntity<ApiResponse<PartnerMeterResponse>> findById(
            @PathVariable Long asignacionId
    ) {
        log.info("Asignacion a consultar: {}", asignacionId);
        PartnerMeterResponse response = partnerMeterService.findById(asignacionId);

        return ResponseEntity.ok(
                ApiResponse.success("Asignación encontrada", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartnerMeterResponse>>> findAll() {
        List<PartnerMeterResponse> response = partnerMeterService.findAll();

        return ResponseEntity.ok(
                ApiResponse.success("Asignaciones consultadas correctamente", response)
        );
    }

    @GetMapping("/socios/{socioId}")
    public ResponseEntity<ApiResponse<List<PartnerMeterResponse>>> findBySocioId(
            @PathVariable Long socioId
    ) {
        List<PartnerMeterResponse> response = partnerMeterService.findByPartnerId(socioId);

        return ResponseEntity.ok(
                ApiResponse.success("Asignaciones del socio consultadas correctamente", response)
        );
    }

    @GetMapping("/medidores/{medidorId}")
    public ResponseEntity<ApiResponse<List<PartnerMeterResponse>>> findByMedidorId(
            @PathVariable Long medidorId
    ) {
        List<PartnerMeterResponse> response = partnerMeterService.findByMeterId(medidorId);

        return ResponseEntity.ok(
                ApiResponse.success("Asignaciones del medidor consultadas correctamente", response)
        );
    }

    @PutMapping("/{asignacionId}")
    public ResponseEntity<ApiResponse<PartnerMeterResponse>> update(
            @PathVariable Long asignacionId,
            @Valid @RequestBody UpdatePartnerMeterRequest request
    ) {
        PartnerMeterResponse response = partnerMeterService.update(asignacionId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Asignación actualizada correctamente", response)
        );
    }

    @DeleteMapping("/{asignacionId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long asignacionId
    ) {
        partnerMeterService.delete(asignacionId);

        return ResponseEntity.ok(
                ApiResponse.success("Asignación retirada correctamente", null)
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            log.warn("Could not serialize request body to JSON", ex);
            return String.valueOf(value);
        }
    }
}
