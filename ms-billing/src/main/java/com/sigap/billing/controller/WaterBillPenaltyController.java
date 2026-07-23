package com.sigap.billing.controller;

import com.sigap.billing.dto.ApiResponse;
import com.sigap.billing.dto.penalty.ApplyWaterBillPenaltyRequest;
import com.sigap.billing.dto.penalty.WaterBillPenaltyResponse;
import com.sigap.billing.service.WaterBillPenaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
@RequiredArgsConstructor
public class WaterBillPenaltyController {

    private final WaterBillPenaltyService waterBillPenaltyService;

    @PostMapping("/{billId}/multas")
    public ResponseEntity<ApiResponse<WaterBillPenaltyResponse>> apply(
            @PathVariable Long billId,
            @Valid @RequestBody ApplyWaterBillPenaltyRequest request
    ) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Multa aplicada correctamente", waterBillPenaltyService.apply(billId, request)));
    }

    @GetMapping("/{billId}/multas")
    public ResponseEntity<ApiResponse<List<WaterBillPenaltyResponse>>> findByBillId(
            @PathVariable Long billId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Multas de la factura consultadas correctamente", waterBillPenaltyService.findByBillId(billId))
        );
    }
}
