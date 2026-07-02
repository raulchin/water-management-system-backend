package com.sigap.billing.service.impl;

import com.sigap.billing.client.ReadingClient;
import com.sigap.billing.dto.*;
import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.enums.WaterBillStatus;
import com.sigap.billing.exception.BadRequestException;
import com.sigap.billing.exception.DuplicateResourceException;
import com.sigap.billing.exception.ResourceNotFoundException;
import com.sigap.billing.repository.WaterBillRepository;
import com.sigap.billing.service.WaterBillService;
import feign.FeignException;
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
public class WaterBillServiceImpl implements WaterBillService {

    private final WaterBillRepository waterBillRepository;
    private final ReadingClient readingClient;


    @Override
    @Transactional
    public WaterBillResponse create(CreateWaterBillRequest request) {

        log.info("Inicia el proceso para crear una nueva factura. Socio={}",request.partnerName());
        MeterReadingResponse reading = findReading(request.readingId());

        if (waterBillRepository.existsByReadingId(reading.readingId())) {
            log.warn("Ya existe una factura para la lectura {}", reading.readingId());
            throw new DuplicateResourceException("Ya existe una factura para la lectura " + reading.readingId());
        }

        if (waterBillRepository.existsByMeterIdAndPeriod(reading.meterId(), reading.period())) {
            throw new DuplicateResourceException("Ya existe una factura para el medidor y periodo indicados");
        }

        validateAmounts(request);

        WaterBillEntity entity = WaterBillEntity.builder()
                .readingId(reading.readingId())
                .meterId(reading.meterId())
                .assignmentId(reading.assignmentId())
                .partnerId(reading.partnerId())
                .period(reading.period())
                .partnerIdentification(reading.partnerIdentification())
                .partnerName(normalize(request.partnerName()))
                .meterNumber(reading.meterNumber())
                .calculatedConsumption(reading.calculatedConsumption())
                .baseFee(request.baseFee())
                .consumptionAmount(request.consumptionAmount())
                .penaltyAmount(defaultZero(request.penaltyAmount()))
                .discountAmount(defaultZero(request.discountAmount()))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(request.dueDate())
                .status(WaterBillStatus.PENDIENTE)
                .observation(normalize(request.observation()))
                .partnerIdentification(request.partnerIdentification())
                .build();
        log.info("Factura creada con exito.");
        return toResponse(waterBillRepository.save(entity));
    }

    @Override
    public WaterBillResponse findById(Long billId) {
        log.info("Ejecutar la consulta en la base del idFactura.");
        return toResponse(findBill(billId));
    }

    @Override
    public List<WaterBillResponse> findByPartnerAndPeriod(String identification, String period) {
        log.info("Ejecutar la consulta en la base con el socio y el periodo.");
        return waterBillRepository.findByPartnerIdentificationAndPeriod(normalize(identification), normalize(period))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<WaterBillResponse> findByMeterAndPeriod(String meterNumber, String period) {
        return waterBillRepository.findByMeterNumberAndPeriod(normalize(meterNumber), normalize(period))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<WaterBillResponse> findPendingByPartner(String identification) {
        return waterBillRepository.findByPartnerIdentificationAndStatusIn(
                        normalize(identification),
                        List.of(WaterBillStatus.PENDIENTE, WaterBillStatus.PAGO_PARCIAL, WaterBillStatus.VENCIDA)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WaterBillResponse update(Long billId, UpdateWaterBillRequest request) {
        WaterBillEntity entity = findBill(billId);

        if (WaterBillStatus.ANULADA.equals(entity.getStatus())) {
            throw new BadRequestException("No se puede actualizar una factura anulada");
        }

        if (entity.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("No se pueden modificar valores de una factura con pagos registrados");
        }

        if (request.penaltyAmount() != null) {
            entity.setPenaltyAmount(request.penaltyAmount());
        }

        if (request.discountAmount() != null) {
            entity.setDiscountAmount(request.discountAmount());
        }

        if (request.dueDate() != null) {
            entity.setDueDate(request.dueDate());
        }

        if (request.observation() != null) {
            entity.setObservation(normalize(request.observation()));
        }

        validateBillAmounts(entity);

        return toResponse(waterBillRepository.save(entity));
    }

    @Override
    @Transactional
    public void cancel(Long billId) {
        WaterBillEntity entity = findBill(billId);

        if (WaterBillStatus.PAGADA.equals(entity.getStatus())) {
            throw new BadRequestException("No se puede anular una factura pagada");
        }

        entity.setStatus(WaterBillStatus.ANULADA);
        waterBillRepository.save(entity);
    }

    private MeterReadingResponse findReading(Long readingId) {
        try {
            ApiResponse<MeterReadingResponse> response = readingClient.findById(readingId);

            if (response == null || response.data() == null) {
                throw new ResourceNotFoundException("No existe la lectura con id " + readingId);
            }

            return response.data();

        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("No existe la lectura con id " + readingId);
        }
    }

    private WaterBillEntity findBill(Long billId) {
        return waterBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + billId));
    }

    private void validateAmounts(CreateWaterBillRequest request) {
        BigDecimal subtotal = request.baseFee()
                .add(request.consumptionAmount())
                .add(defaultZero(request.penaltyAmount()));

        if (defaultZero(request.discountAmount()).compareTo(subtotal) > 0) {
            throw new BadRequestException("El descuento no puede ser mayor al subtotal de la factura");
        }
    }

    private void validateBillAmounts(WaterBillEntity entity) {
        BigDecimal subtotal = entity.getBaseFee()
                .add(entity.getConsumptionAmount())
                .add(defaultZero(entity.getPenaltyAmount()));

        if (defaultZero(entity.getDiscountAmount()).compareTo(subtotal) > 0) {
            throw new BadRequestException("El descuento no puede ser mayor al subtotal de la factura");
        }
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private WaterBillResponse toResponse(WaterBillEntity entity) {
        return new WaterBillResponse(
                entity.getBillId(),
                entity.getReadingId(),
                entity.getMeterId(),
                entity.getAssignmentId(),
                entity.getPartnerId(),
                entity.getPeriod(),
                entity.getPartnerIdentification(),
                entity.getPartnerName(),
                entity.getMeterNumber(),
                entity.getCalculatedConsumption(),
                entity.getBaseFee(),
                entity.getConsumptionAmount(),
                entity.getPenaltyAmount(),
                entity.getDiscountAmount(),
                entity.getTotalAmount(),
                entity.getPaidAmount(),
                entity.getPendingBalance(),
                entity.getIssueDate(),
                entity.getDueDate(),
                entity.getStatus().name(),
                entity.getObservation(),
                entity.getCreationDate(),
                entity.getUpdateDate()
        );
    }
}