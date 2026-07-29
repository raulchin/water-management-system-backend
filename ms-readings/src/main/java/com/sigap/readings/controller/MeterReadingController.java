package com.sigap.readings.controller;

import com.sigap.readings.dto.*;
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
    public ResponseEntity<ApiResponse<PageResponse<MeterReadingResponse>>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Consultar lecturas paginadas. page={}, size={}", page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lecturas de medidor consultadas correctamente",
                        meterReadingService.findAllPaged(page, size)
                )
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

    @GetMapping("/medidor/{meterId}/previous")
    public ResponseEntity<ApiResponse<PreviousMeterReadingResponse>> findPreviousByMeterIdAndPeriod(
            @PathVariable Long meterId,
            @RequestParam String period
    ) {
        log.info("Consultar lectura anterior. meterId={}, period={}", meterId, period);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lectura anterior consultada correctamente",
                        meterReadingService.findPreviousByMeterIdAndPeriod(meterId, period)
                )
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MeterReadingSearchResponse>>> search(
            @RequestParam(required = false) String identification,
            @RequestParam(required = false) String meterNumber,
            @RequestParam String period
    ) {
        log.info("Buscar lecturas de los medidores...");
        List<MeterReadingSearchResponse> response =
                meterReadingService.searchByIdentificationOrMeterNumber(
                        identification,
                        meterNumber,
                        period
                );

        return ResponseEntity.ok(
                ApiResponse.success("Lecturas consultadas correctamente", response)
        );
    }


}
