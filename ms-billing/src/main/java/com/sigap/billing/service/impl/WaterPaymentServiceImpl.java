package com.sigap.billing.service.impl;


import com.sigap.billing.dto.*;
import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.entity.WaterPaymentEntity;
import com.sigap.billing.enums.WaterBillStatus;
import com.sigap.billing.exception.BadRequestException;
import com.sigap.billing.exception.ResourceNotFoundException;
import com.sigap.billing.repository.WaterBillRepository;
import com.sigap.billing.repository.WaterPaymentRepository;
import com.sigap.billing.service.WaterPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WaterPaymentServiceImpl implements WaterPaymentService {

    private final WaterPaymentRepository waterPaymentRepository;

    private final WaterBillRepository waterBillRepository;

    @Override
    @Transactional
    public WaterPaymentResponse create(CreateWaterPaymentRequest request) {

        log.info("Registrando cobro de factura. billId={}, amount={}", request.billId(), request.paymentAmount());

        WaterBillEntity bill = waterBillRepository.findByIdForUpdate(request.billId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + request.billId()));

        validateBillCanBePaid(bill);
        validatePaymentAmount(request.paymentAmount(), bill.getPendingBalance());
        String reference = generatePaymentReference();
        WaterPaymentEntity payment = WaterPaymentEntity.builder()
                .billId(bill.getBillId())
                .partnerId(bill.getPartnerId())
                .meterId(bill.getMeterId())
                .period(bill.getPeriod())
                .paidValue(request.paymentAmount())
                .paymentMethod(request.paymentMethod())
                .paymentReference(reference)
                .status("REGISTRADO")
                .paymentDate(request.paymentDate())
                .observation(normalize(request.observation()))
                .partnerIdentification(bill.getPartnerIdentification())
                .meterNumber(bill.getMeterNumber())
                .build();

        WaterPaymentEntity savedPayment = waterPaymentRepository.save(payment);
        BigDecimal newPaidAmount = bill.getPaidAmount().add(request.paymentAmount());
        BigDecimal newPendingBalance = bill.getTotalAmount().subtract(newPaidAmount);

        bill.setPaidAmount(newPaidAmount);

        if (newPendingBalance.compareTo(BigDecimal.ZERO) == 0) {
            bill.setStatus(WaterBillStatus.PAGADA);
        } else {
            bill.setStatus(WaterBillStatus.PAGO_PARCIAL);
        }

        WaterBillEntity updatedBill = waterBillRepository.save(bill);

        log.info(
                "Cobro registrado correctamente. billId={}, paymentId={}, status={}",
                updatedBill.getBillId(),
                savedPayment.getPaymentId(),
                updatedBill.getStatus()
        );

        return toResponse(savedPayment, updatedBill);
    }

    @Override
    public List<WaterPaymentResponse> findByBillId(Long billId) {

        WaterBillEntity bill = waterBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + billId));

        return waterPaymentRepository.findByBillIdOrderByPaymentDateDesc(billId)
                .stream()
                .map(payment -> toResponse(payment, bill))
                .toList();
    }

    @Override
    public List<WaterPaymentResponse> findLast10() {
        log.info("Consultando los 10 ultimos cobros registrados.");

        return waterPaymentRepository.findTop10ByOrderByCreationDateDesc()
                .stream()
                .map(this::toResponseWithoutBill)
                .toList();
    }

    @Override
    @Transactional
    public BatchWaterPaymentResponse createBatch(CreateBatchWaterPaymentRequest request) {

        String reference = generatePaymentReference();
        LocalDate paymentDate = request.paymentDate() == null ? LocalDate.now() : request.paymentDate();

        List<WaterPaymentResponse> payments = request.items()
                .stream()
                .map(item -> createPaymentFromBatch(request, item, reference, paymentDate))
                .toList();

        BigDecimal totalPaidAmount = payments.stream()
                .map(WaterPaymentResponse::paymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BatchWaterPaymentResponse(
                reference,
                request.paymentMethod().name(),
                paymentDate,
                totalPaidAmount,
                payments
        );
    }

    private WaterPaymentResponse createPaymentFromBatch(
            CreateBatchWaterPaymentRequest request,
            BatchWaterPaymentItemRequest item,
            String reference,
            LocalDate paymentDate
    ) {
        WaterBillEntity bill = waterBillRepository.findByIdForUpdate(item.billId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + item.billId()));

        validateBillCanBePaid(bill);
        validatePaymentAmount(item.paymentAmount(), bill.getPendingBalance());

        WaterPaymentEntity payment = WaterPaymentEntity.builder()
                .billId(bill.getBillId())
                .partnerId(bill.getPartnerId())
                .meterId(bill.getMeterId())
                .period(bill.getPeriod())
                .paidValue(item.paymentAmount())
                .paymentMethod(request.paymentMethod())
                .paymentReference(reference)
                .status("REGISTRADO")
                .paymentDate(paymentDate)
                .observation(normalize(request.observation()))
                .partnerIdentification(bill.getPartnerIdentification())
                .meterNumber(bill.getMeterNumber())
                .build();

        WaterPaymentEntity savedPayment = waterPaymentRepository.save(payment);

        BigDecimal newPaidAmount = bill.getPaidAmount().add(item.paymentAmount());
        BigDecimal newPendingBalance = bill.getTotalAmount().subtract(newPaidAmount);

        bill.setPaidAmount(newPaidAmount);

        if (newPendingBalance.compareTo(BigDecimal.ZERO) == 0) {
            bill.setStatus(WaterBillStatus.PAGADA);
        } else {
            bill.setStatus(WaterBillStatus.PAGO_PARCIAL);
        }

        WaterBillEntity updatedBill = waterBillRepository.save(bill);

        return toResponse(savedPayment, updatedBill);
    }

    private void validateBillCanBePaid(WaterBillEntity bill) {
        if (WaterBillStatus.PAGADA.equals(bill.getStatus())) {
            throw new BadRequestException("La factura ya se encuentra pagada");
        }

        if (WaterBillStatus.ANULADA.equals(bill.getStatus())) {
            throw new BadRequestException("No se puede cobrar una factura anulada");
        }
    }

    private void validatePaymentAmount(BigDecimal paymentAmount, BigDecimal pendingBalance) {
        if (paymentAmount.compareTo(pendingBalance) > 0) {
            throw new BadRequestException("El monto pagado no puede ser mayor al saldo pendiente");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private WaterPaymentResponse toResponse(WaterPaymentEntity payment, WaterBillEntity bill) {
        return new WaterPaymentResponse(
                payment.getPaymentId(),
                payment.getBillId(),
                payment.getPartnerId(),
                payment.getMeterId(),
                payment.getPartnerIdentification(),
                payment.getMeterNumber(),
                payment.getPeriod(),
                payment.getPaidValue(),
                payment.getPaymentMethod().name(),
                payment.getPaymentReference(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getObservation(),
                bill.getTotalAmount(),
                bill.getPaidAmount(),
                bill.getPendingBalance(),
                bill.getStatus().name(),
                payment.getCreationDate()
        );
    }

    private WaterPaymentResponse toResponseWithoutBill(WaterPaymentEntity payment) {
        return new WaterPaymentResponse(
                payment.getPaymentId(),
                payment.getBillId(),
                payment.getPartnerId(),
                payment.getMeterId(),
                payment.getPartnerIdentification(),
                payment.getMeterNumber(),
                payment.getPeriod(),
                payment.getPaidValue(),
                payment.getPaymentMethod().name(),
                payment.getPaymentReference(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getObservation(),
                null,
                null,
                null,
                null,
                payment.getCreationDate()
        );
    }

    private String generatePaymentReference() {
        return "REC-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
