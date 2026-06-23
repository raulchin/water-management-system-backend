package com.sigap.readings.controller;

import com.sigap.readings.dto.ApiResponse;
import com.sigap.readings.dto.CreateMeterReadingRequest;
import com.sigap.readings.dto.MeterReadingResponse;
import com.sigap.readings.dto.UpdateMeterReadingRequest;
import com.sigap.readings.service.MeterReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lecturas-medidor")
@RequiredArgsConstructor
@Slf4j
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    @PostMapping
    public ResponseEntity<ApiResponse<MeterReadingResponse>> create(
            @Valid @RequestBody CreateMeterReadingRequest request
    ) {
        log.info("Proceso para registrar una lectura del medidor: {}",request.meterId());
        MeterReadingResponse response = meterReadingService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.readingId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ApiResponse.success("Lectura de medidor registrada correctamente", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> findAll() {
        log.info("Proceso para obtener las lectura de los medidores..");
        return ResponseEntity.ok(
                ApiResponse.success("Lecturas de medidor consultadas correctamente", meterReadingService.findAll())
        );
    }

    @GetMapping("/{readingId}")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> findById(@PathVariable Long readingId) {
        return ResponseEntity.ok(
                ApiResponse.success("Lectura de medidor consultada correctamente", meterReadingService.findById(readingId))
        );
    }

    @GetMapping("/medidor/{meterId}")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> findByMeterId(@PathVariable Long meterId) {
        return ResponseEntity.ok(
                ApiResponse.success("Lecturas de medidor consultadas correctamente", meterReadingService.findByMeterId(meterId))
        );
    }

    @PutMapping("/{readingId}")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> update(
            @PathVariable Long readingId,
            @Valid @RequestBody UpdateMeterReadingRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Lectura de medidor actualizada correctamente", meterReadingService.update(readingId, request))
        );
    }

    @DeleteMapping("/{readingId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long readingId) {
        meterReadingService.delete(readingId);
        return ResponseEntity.ok(
                ApiResponse.success("Lectura de medidor eliminada correctamente", null)
        );
    }
}
