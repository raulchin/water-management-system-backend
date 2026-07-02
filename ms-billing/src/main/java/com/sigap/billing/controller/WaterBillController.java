package com.sigap.billing.controller;

import com.sigap.billing.dto.ApiResponse;
import com.sigap.billing.dto.CreateWaterBillRequest;
import com.sigap.billing.dto.UpdateWaterBillRequest;
import com.sigap.billing.dto.WaterBillResponse;
import com.sigap.billing.service.WaterBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
@RequiredArgsConstructor
@Slf4j
public class WaterBillController {

    private final WaterBillService waterBillService;

    @PostMapping
    public ResponseEntity<ApiResponse<WaterBillResponse>> create(
            @Valid @RequestBody CreateWaterBillRequest request
    ) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Factura generada correctamente", waterBillService.create(request)));
    }

    @GetMapping("/{billId}")
    public ResponseEntity<ApiResponse<WaterBillResponse>> findById(@PathVariable Long billId) {
        log.info("Consultar la factura con el ID={}", billId);
        return ResponseEntity.ok(
                ApiResponse.success("Factura consultada correctamente", waterBillService.findById(billId))
        );
    }

    @GetMapping("/by-partner")
    public ResponseEntity<ApiResponse<List<WaterBillResponse>>> findByPartnerAndPeriod(
            @RequestParam String identification,
            @RequestParam String period
    ) {
        log.info("Consultar la factura de un Socio={}, Perido={}",identification, period);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Facturas del socio consultadas correctamente",
                        waterBillService.findByPartnerAndPeriod(identification, period)
                )
        );
    }

    @GetMapping("/by-meter")
    public ResponseEntity<ApiResponse<List<WaterBillResponse>>> findByMeterAndPeriod(
            @RequestParam String meterNumber,
            @RequestParam String period
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Facturas del medidor consultadas correctamente",
                        waterBillService.findByMeterAndPeriod(meterNumber, period)
                )
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<WaterBillResponse>>> findPendingByPartner(
            @RequestParam String identification
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Facturas pendientes consultadas correctamente",
                        waterBillService.findPendingByPartner(identification)
                )
        );
    }

    @PatchMapping("/{billId}")
    public ResponseEntity<ApiResponse<WaterBillResponse>> update(
            @PathVariable Long billId,
            @Valid @RequestBody UpdateWaterBillRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Factura actualizada correctamente", waterBillService.update(billId, request))
        );
    }

    @PatchMapping("/{billId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long billId) {
        waterBillService.cancel(billId);

        return ResponseEntity.ok(
                ApiResponse.success("Factura anulada correctamente", null)
        );
    }
}