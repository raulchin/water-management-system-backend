package com.sigap.partner.controller;

import com.sigap.partner.dto.account.ContractAccountCreateRequest;
import com.sigap.partner.dto.account.ContractAccountResponse;
import com.sigap.partner.dto.account.ContractAccountUpdateRequest;
import com.sigap.partner.dto.common.ApiResponse;
import com.sigap.partner.service.ContractAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contract-accounts")
public class ContractAccountController {

    private final ContractAccountService contractAccountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ContractAccountResponse>> create(
            @Valid @RequestBody ContractAccountCreateRequest request
    ) {
        ContractAccountResponse response = contractAccountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cuenta contrato creada correctamente", response));
    }

    @GetMapping("/api/v1/partners/{partnerId}/contract-accounts")
    public ResponseEntity<ApiResponse<List<ContractAccountResponse>>> findByPartner(@PathVariable Long partnerId) {
        List<ContractAccountResponse> response = contractAccountService.findByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/findById/{contractAccountId}")
    public ResponseEntity<ApiResponse<ContractAccountResponse>> findById(@PathVariable Long contractAccountId) {
        ContractAccountResponse response = contractAccountService.findById(contractAccountId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/update/{contractAccountId}")
    public ResponseEntity<ApiResponse<ContractAccountResponse>> update(
            @PathVariable Long contractAccountId,
            @Valid @RequestBody ContractAccountUpdateRequest request
    ) {
        ContractAccountResponse response = contractAccountService.update(contractAccountId, request);
        return ResponseEntity.ok(ApiResponse.ok("Cuenta contrato actualizada correctamente", response));
    }
}
