package com.sigap.meters.controller;

import com.sigap.meters.dto.ApiResponse;
import com.sigap.meters.dto.CreateMeterRequest;
import com.sigap.meters.dto.MeterResponse;
import com.sigap.meters.service.MeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/medidores")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    public ResponseEntity<ApiResponse<MeterResponse>> registerMeter(
            @Valid @RequestBody CreateMeterRequest request
    ) {

        MeterResponse response = meterService.registerMeter(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.medidorId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ApiResponse.success("Medidor registrado correctamente", response));

    }
}
