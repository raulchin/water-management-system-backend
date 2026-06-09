package com.sigap.meters.controller;

import com.sigap.meters.dto.ApiResponse;
import com.sigap.meters.dto.CreatePartnerMeterRequest;
import com.sigap.meters.dto.PartnerMeterResponse;
import com.sigap.meters.dto.UpdatePartnerMeterRequest;
import com.sigap.meters.service.PartnerMeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medidor-socios")
@RequiredArgsConstructor
public class PartnerMeterController {

    private final PartnerMeterService partnerMeterService;

    @PostMapping
    public ResponseEntity<ApiResponse<PartnerMeterResponse>> create(
            @Valid @RequestBody CreatePartnerMeterRequest request
    ) {
        PartnerMeterResponse response = partnerMeterService.create(request);

        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Medidor asignado correctamente al socio", response));
    }

    @GetMapping("/{asignacionId}")
    public ResponseEntity<ApiResponse<PartnerMeterResponse>> findById(
            @PathVariable Long asignacionId
    ) {
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
}
