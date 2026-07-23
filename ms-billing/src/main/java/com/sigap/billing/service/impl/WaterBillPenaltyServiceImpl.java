package com.sigap.billing.service.impl;

import com.sigap.billing.dto.penalty.ApplyWaterBillPenaltyRequest;
import com.sigap.billing.dto.penalty.WaterBillPenaltyResponse;
import com.sigap.billing.entity.PenaltyTypeEntity;
import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.entity.WaterBillPenaltyEntity;
import com.sigap.billing.enums.PenaltyStatus;
import com.sigap.billing.enums.WaterBillStatus;
import com.sigap.billing.exception.BadRequestException;
import com.sigap.billing.exception.ResourceNotFoundException;
import com.sigap.billing.repository.PenaltyTypeRepository;
import com.sigap.billing.repository.WaterBillPenaltyRepository;
import com.sigap.billing.repository.WaterBillRepository;
import com.sigap.billing.service.WaterBillPenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WaterBillPenaltyServiceImpl implements WaterBillPenaltyService {

    private final WaterBillRepository waterBillRepository;
    private final PenaltyTypeRepository penaltyTypeRepository;
    private final WaterBillPenaltyRepository waterBillPenaltyRepository;

    @Override
    @Transactional
    public WaterBillPenaltyResponse apply(Long billId, ApplyWaterBillPenaltyRequest request) {
        log.info("Aplicando multa a factura. billId={}, penaltyTypeId={}", billId, request.penaltyTypeId());

        WaterBillEntity bill = waterBillRepository.findByIdForUpdate(billId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + billId));

        validateBillCanReceivePenalty(bill);

        PenaltyTypeEntity penaltyType = penaltyTypeRepository.findByPenaltyTypeIdAndActiveTrue(request.penaltyTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un tipo de multa activo con id " + request.penaltyTypeId()));

        BigDecimal penaltyAmount = request.amount() == null ? penaltyType.getBaseAmount() : request.amount();

        if (penaltyAmount == null || penaltyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto de la multa debe ser mayor a cero");
        }

        WaterBillPenaltyEntity penalty = WaterBillPenaltyEntity.builder()
                .billId(bill.getBillId())
                .penaltyTypeId(penaltyType.getPenaltyTypeId())
                .partnerId(bill.getPartnerId())
                .meterId(bill.getMeterId())
                .period(bill.getPeriod())
                .partnerIdentification(bill.getPartnerIdentification())
                .meterNumber(bill.getMeterNumber())
                .penaltyCode(penaltyType.getCode())
                .penaltyName(penaltyType.getName())
                .amount(penaltyAmount)
                .status(PenaltyStatus.ACTIVA)
                .observation(normalize(request.observation()))
                .build();

        WaterBillPenaltyEntity savedPenalty = waterBillPenaltyRepository.save(penalty);

        BigDecimal currentPenaltyAmount = bill.getPenaltyAmount() == null
                ? BigDecimal.ZERO
                : bill.getPenaltyAmount();

        bill.setPenaltyAmount(currentPenaltyAmount.add(penaltyAmount));

        WaterBillEntity updatedBill = waterBillRepository.save(bill);

        log.info(
                "Multa aplicada correctamente. billId={}, penaltyId={}, penaltyAmount={}, totalPenaltyAmount={}, pendingBalance={}",
                updatedBill.getBillId(),
                savedPenalty.getBillPenaltyId(),
                penaltyAmount,
                updatedBill.getPenaltyAmount(),
                updatedBill.getPendingBalance()
        );

        return toResponse(savedPenalty);
    }

    @Override
    public List<WaterBillPenaltyResponse> findByBillId(Long billId) {
        if (!waterBillRepository.existsById(billId)) {
            throw new ResourceNotFoundException("No existe la factura con id " + billId);
        }

        return waterBillPenaltyRepository.findByBillIdOrderByCreationDateDesc(billId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateBillCanReceivePenalty(WaterBillEntity bill) {
        if (WaterBillStatus.PAGADA.equals(bill.getStatus())) {
            throw new BadRequestException("No se puede aplicar multa a una factura pagada");
        }

        if (WaterBillStatus.ANULADA.equals(bill.getStatus())) {
            throw new BadRequestException("No se puede aplicar multa a una factura anulada");
        }
    }

    private WaterBillPenaltyResponse toResponse(WaterBillPenaltyEntity entity) {
        return new WaterBillPenaltyResponse(
                entity.getBillPenaltyId(),
                entity.getBillId(),
                entity.getPenaltyTypeId(),
                entity.getPenaltyCode(),
                entity.getPenaltyName(),
                entity.getAmount(),
                entity.getStatus().name(),
                entity.getObservation(),
                entity.getApplicationDate(),
                entity.getCreationDate()
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
