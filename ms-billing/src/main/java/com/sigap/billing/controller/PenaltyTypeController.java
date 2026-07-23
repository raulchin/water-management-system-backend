package com.sigap.billing.controller;

import com.sigap.billing.dto.ApiResponse;
import com.sigap.billing.dto.penalty.CreatePenaltyTypeRequest;
import com.sigap.billing.dto.penalty.PenaltyTypeResponse;
import com.sigap.billing.service.PenaltyTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-multa")
@RequiredArgsConstructor
public class PenaltyTypeController {

    private final PenaltyTypeService penaltyTypeService;

    @PostMapping
    public ResponseEntity<ApiResponse<PenaltyTypeResponse>> create(
            @Valid @RequestBody CreatePenaltyTypeRequest request
    ) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Tipo de multa creado correctamente", penaltyTypeService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PenaltyTypeResponse>>> findActive() {
        return ResponseEntity.ok(
                ApiResponse.success("Tipos de multa consultados correctamente", penaltyTypeService.findActive())
        );
    }
}
