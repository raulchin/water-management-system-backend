package com.sigap.billing.controller;


import com.sigap.billing.dto.*;
import com.sigap.billing.service.WaterPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cobros")
@RequiredArgsConstructor
@Slf4j
public class WaterPaymentController {

    private final WaterPaymentService waterPaymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<WaterPaymentResponse>> create(
            @Valid @RequestBody CreateWaterPaymentRequest request
    ) {
        log.info("Registrar cobro de factura. billId={}", request.billId());

        return ResponseEntity.status(201)
                .body(ApiResponse.success(
                        "Cobro registrado correctamente",
                        waterPaymentService.create(request)
                ));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<BatchWaterPaymentResponse>> createBatch(
            @Valid @RequestBody CreateBatchWaterPaymentRequest request
    ) {
        log.info("Registrar cobro de factura Batch.");

        return ResponseEntity.status(201)
                .body(ApiResponse.success(
                        "Cobro registrado correctamente",
                        waterPaymentService.createBatch(request)
                ));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ItemWaterPaymentResponse>> createByItems(
            @Valid @RequestBody CreateItemWaterPaymentRequest request
    ) {
        log.info("Registrar cobro por items. itemsCount={}", request.items().size());

        return ResponseEntity.status(201)
                .body(ApiResponse.success(
                        "Cobro por items registrado correctamente",
                        waterPaymentService.createByItems(request)
                ));
    }

    @GetMapping("/items-pendientes")
    public ResponseEntity<ApiResponse<List<PendingPaymentBillResponse>>> findPendingItemsByPartner(
            @RequestParam String identification
    ) {
        log.info("Consultar items pendientes de cobro. identification={}", identification);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Items pendientes consultados correctamente",
                        waterPaymentService.findPendingItemsByPartner(identification)
                )
        );
    }

    @GetMapping("/factura/{billId}")
    public ResponseEntity<ApiResponse<List<WaterPaymentResponse>>> findByBillId(
            @PathVariable Long billId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cobros de la factura consultados correctamente",
                        waterPaymentService.findByBillId(billId)
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<WaterPaymentResponse>>> findLast10() {
        log.info("Consultar los 10 ultimos cobros registrados.");

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ultimos cobros consultados correctamente",
                        waterPaymentService.findLast10()
                )
        );
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<WaterPaymentResponse>>> findAllPaymentItems() {
        log.info("Consultar todos los items de cobro registrados.");

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Items de cobro consultados correctamente",
                        waterPaymentService.findAllPaymentItems()
                )
        );
    }

}
