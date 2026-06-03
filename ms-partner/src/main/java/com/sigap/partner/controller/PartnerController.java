package com.sigap.partner.controller;

import com.sigap.partner.dto.common.ApiResponse;
import com.sigap.partner.dto.partner.PartnerCreateRequest;
import com.sigap.partner.dto.partner.PartnerResponse;
import com.sigap.partner.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Slf4j
public class PartnerController {

    private final PartnerService partnerService;
    @PostMapping
    public ResponseEntity<ApiResponse<PartnerResponse>> create(@Valid @RequestBody PartnerCreateRequest request) {
        PartnerResponse response = partnerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Socio creado correctamente", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartnerResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(partnerService.findAll()));
    }

    @GetMapping("/{partnerId}")
    public ResponseEntity<ApiResponse<PartnerResponse>> findById(@PathVariable Long partnerId) {
        return ResponseEntity.ok(ApiResponse.ok(partnerService.findById(partnerId)));
    }
}
