package com.sigap.billing.controller;

import com.sigap.billing.dto.*;
import com.sigap.billing.exception.BadRequestException;
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
        log.info("Crear nueva factura para el Socio={}",request.partnerIdentification());
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Factura generada correctamente", waterBillService.create(request)));
    }

    @PostMapping("/from-reading")
    public ResponseEntity<ApiResponse<WaterBillResponse>> createFromReading(
            @Valid @RequestBody CreateWaterBillFromReadingRequest request
    ) {
        log.info("Crear factura automaticamente desde lectura={}", request.readingId());

        return ResponseEntity.status(201)
                .body(ApiResponse.success(
                        "Factura generada automaticamente desde lectura",
                        waterBillService.createFromReading(request)
                ));
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

    @GetMapping("/from-reading/{readingId}/can-recalculate")
    public ResponseEntity<ApiResponse<Void>> validateCanRecalculateFromReading(
            @PathVariable Long readingId
    ) {
        log.info("Validar si factura puede recalcularse desde lectura. readingId={}", readingId);

        waterBillService.validateCanRecalculateFromReading(readingId);

        return ResponseEntity.ok(
                ApiResponse.success("La factura puede recalcularse", null)
        );
    }

    @PostMapping("/from-reading/{readingId}/recalculate")
    public ResponseEntity<ApiResponse<WaterBillResponse>> recalculateFromReading(
            @PathVariable Long readingId,
            @Valid @RequestBody RecalculateWaterBillFromReadingRequest request
    ) {
        log.info("Recalcular factura desde lectura actualizada. readingId={}", readingId);

        if (!readingId.equals(request.readingId())) {
            throw new BadRequestException("El readingId de la URL no coincide con el request");
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Factura recalculada correctamente desde lectura",
                        waterBillService.recalculateFromReading(request)
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

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<WaterBillResponse>>> findLast10() {
        log.info("Consultar las 10 ultimas facturas registradas.");

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ultimas facturas consultadas correctamente",
                        waterBillService.findLast10()
                )
        );
    }


}