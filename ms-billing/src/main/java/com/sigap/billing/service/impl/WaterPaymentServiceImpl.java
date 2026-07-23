package com.sigap.billing.service.impl;


import com.sigap.billing.dto.*;
import com.sigap.billing.entity.WaterBillEntity;
import com.sigap.billing.entity.WaterBillPenaltyEntity;
import com.sigap.billing.entity.WaterPaymentDetailEntity;
import com.sigap.billing.entity.WaterPaymentEntity;
import com.sigap.billing.enums.PaymentItemType;
import com.sigap.billing.enums.PenaltyStatus;
import com.sigap.billing.enums.WaterBillStatus;
import com.sigap.billing.exception.BadRequestException;
import com.sigap.billing.exception.ResourceNotFoundException;
import com.sigap.billing.repository.WaterBillPenaltyRepository;
import com.sigap.billing.repository.WaterBillRepository;
import com.sigap.billing.repository.WaterPaymentDetailRepository;
import com.sigap.billing.repository.WaterPaymentRepository;
import com.sigap.billing.service.WaterPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WaterPaymentServiceImpl implements WaterPaymentService {

    private final WaterPaymentRepository waterPaymentRepository;

    private final WaterBillRepository waterBillRepository;

    private final WaterPaymentDetailRepository waterPaymentDetailRepository;

    private final WaterBillPenaltyRepository waterBillPenaltyRepository;

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

    @Override
    @Transactional
    public ItemWaterPaymentResponse createByItems(CreateItemWaterPaymentRequest request) {
        log.info("Inicia registro de cobro por items. itemsCount={}", request.items().size());

        validateRepeatedItems(request);

        String reference = generatePaymentReference();
        LocalDate paymentDate = request.paymentDate() == null ? LocalDate.now() : request.paymentDate();

        List<WaterPaymentResponse> payments = new ArrayList<>();
        List<WaterPaymentDetailResponse> details = new ArrayList<>();

        for (CreateItemWaterPaymentDetailRequest item : request.items()) {
            log.info(
                    "Procesando item de cobro. billId={}, billPenaltyId={}, itemType={}, amount={}",
                    item.billId(),
                    item.billPenaltyId(),
                    item.itemType(),
                    item.paymentAmount()
            );

            WaterBillEntity bill = waterBillRepository.findByIdForUpdate(item.billId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la factura con id " + item.billId()));

            validateBillCanBePaid(bill);

            PendingItemData pendingItem = resolvePendingItem(bill, item);

            validatePaymentAmount(item.paymentAmount(), pendingItem.pendingAmount());

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

            WaterPaymentDetailEntity detail = WaterPaymentDetailEntity.builder()
                    .paymentId(savedPayment.getPaymentId())
                    .billId(bill.getBillId())
                    .billPenaltyId(item.billPenaltyId())
                    .itemType(item.itemType())
                    .description(pendingItem.description())
                    .paymentAmount(item.paymentAmount())
                    .build();

            WaterPaymentDetailEntity savedDetail = waterPaymentDetailRepository.save(detail);

            BigDecimal newPaidAmount = bill.getPaidAmount().add(item.paymentAmount());
            bill.setPaidAmount(newPaidAmount);

            if (bill.getTotalAmount().subtract(newPaidAmount).compareTo(BigDecimal.ZERO) == 0) {
                bill.setStatus(WaterBillStatus.PAGADA);
            } else {
                bill.setStatus(WaterBillStatus.PAGO_PARCIAL);
            }

            WaterBillEntity updatedBill = waterBillRepository.save(bill);

            log.info(
                    "Item cobrado correctamente. paymentId={}, paymentDetailId={}, billId={}, billStatus={}, paidAmount={}, pendingBalance={}",
                    savedPayment.getPaymentId(),
                    savedDetail.getPaymentDetailId(),
                    updatedBill.getBillId(),
                    updatedBill.getStatus(),
                    updatedBill.getPaidAmount(),
                    updatedBill.getPendingBalance()
            );

            payments.add(toResponse(savedPayment, updatedBill));
            details.add(toDetailResponse(savedDetail));
        }

        BigDecimal totalPaidAmount = details.stream()
                .map(WaterPaymentDetailResponse::paymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info(
                "Cobro por items finalizado correctamente. reference={}, totalPaidAmount={}, totalItems={}",
                reference,
                totalPaidAmount,
                details.size()
        );

        return new ItemWaterPaymentResponse(
                reference,
                request.paymentMethod().name(),
                paymentDate,
                totalPaidAmount,
                payments,
                details
        );
    }

    @Override
    public List<PendingPaymentBillResponse> findPendingItemsByPartner(String identification) {
        log.info("Consultando items pendientes de cobro. identification={}", identification);

        List<WaterBillEntity> bills = waterBillRepository.findByPartnerIdentificationAndStatusIn(
                normalize(identification),
                List.of(WaterBillStatus.PENDIENTE, WaterBillStatus.PAGO_PARCIAL, WaterBillStatus.VENCIDA)
        );

        return bills.stream()
                .map(this::toPendingPaymentBillResponse)
                .filter(response -> !response.items().isEmpty())
                .toList();
    }

    private PendingPaymentBillResponse toPendingPaymentBillResponse(WaterBillEntity bill) {
        List<PendingPaymentItemResponse> items = new ArrayList<>();

        BigDecimal serviceAmount = calculateServiceAmount(bill);
        BigDecimal servicePaidAmount = waterPaymentDetailRepository.sumPaidByItem(
                bill.getBillId(),
                PaymentItemType.SERVICIO_AGUA,
                null
        );
        BigDecimal servicePendingAmount = serviceAmount.subtract(servicePaidAmount);

        if (servicePendingAmount.compareTo(BigDecimal.ZERO) > 0) {
            items.add(new PendingPaymentItemResponse(
                    PaymentItemType.SERVICIO_AGUA.name(),
                    bill.getBillId(),
                    "Servicio de agua " + bill.getPeriod(),
                    serviceAmount,
                    servicePaidAmount,
                    servicePendingAmount
            ));
        }

        List<WaterBillPenaltyEntity> penalties =
                waterBillPenaltyRepository.findByBillIdAndStatus(bill.getBillId(), PenaltyStatus.ACTIVA);

        for (WaterBillPenaltyEntity penalty : penalties) {
            BigDecimal penaltyPaidAmount = waterPaymentDetailRepository.sumPaidByItem(
                    bill.getBillId(),
                    PaymentItemType.MULTA,
                    penalty.getBillPenaltyId()
            );

            BigDecimal penaltyPendingAmount = penalty.getAmount().subtract(penaltyPaidAmount);

            if (penaltyPendingAmount.compareTo(BigDecimal.ZERO) > 0) {
                items.add(new PendingPaymentItemResponse(
                        PaymentItemType.MULTA.name(),
                        penalty.getBillPenaltyId(),
                        penalty.getPenaltyName(),
                        penalty.getAmount(),
                        penaltyPaidAmount,
                        penaltyPendingAmount
                ));
            }
        }

        return new PendingPaymentBillResponse(
                bill.getBillId(),
                bill.getPeriod(),
                bill.getPartnerIdentification(),
                bill.getPartnerName(),
                bill.getMeterNumber(),
                bill.getStatus().name(),
                items
        );
    }

    private PendingItemData resolvePendingItem(
            WaterBillEntity bill,
            CreateItemWaterPaymentDetailRequest item
    ) {
        if (PaymentItemType.SERVICIO_AGUA.equals(item.itemType())) {
            if (item.billPenaltyId() != null) {
                throw new BadRequestException("Para SERVICIO_AGUA no debe enviar billPenaltyId");
            }

            BigDecimal amount = calculateServiceAmount(bill);
            BigDecimal paidAmount = waterPaymentDetailRepository.sumPaidByItem(
                    bill.getBillId(),
                    PaymentItemType.SERVICIO_AGUA,
                    null
            );

            return new PendingItemData(
                    "Servicio de agua " + bill.getPeriod(),
                    amount,
                    paidAmount,
                    amount.subtract(paidAmount)
            );
        }

        if (PaymentItemType.MULTA.equals(item.itemType())) {
            if (item.billPenaltyId() == null) {
                throw new BadRequestException("Para MULTA debe enviar billPenaltyId");
            }

            WaterBillPenaltyEntity penalty = waterBillPenaltyRepository.findById(item.billPenaltyId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la multa con id " + item.billPenaltyId()));

            if (!penalty.getBillId().equals(bill.getBillId())) {
                throw new BadRequestException("La multa no pertenece a la factura indicada");
            }

            if (!PenaltyStatus.ACTIVA.equals(penalty.getStatus())) {
                throw new BadRequestException("La multa no se encuentra activa");
            }

            BigDecimal paidAmount = waterPaymentDetailRepository.sumPaidByItem(
                    bill.getBillId(),
                    PaymentItemType.MULTA,
                    penalty.getBillPenaltyId()
            );

            return new PendingItemData(
                    penalty.getPenaltyName(),
                    penalty.getAmount(),
                    paidAmount,
                    penalty.getAmount().subtract(paidAmount)
            );
        }

        throw new BadRequestException("Tipo de item no soportado");
    }

    private BigDecimal calculateServiceAmount(WaterBillEntity bill) {
        BigDecimal baseFee = bill.getBaseFee() == null ? BigDecimal.ZERO : bill.getBaseFee();
        BigDecimal consumptionAmount = bill.getConsumptionAmount() == null ? BigDecimal.ZERO : bill.getConsumptionAmount();
        BigDecimal discountAmount = bill.getDiscountAmount() == null ? BigDecimal.ZERO : bill.getDiscountAmount();

        return baseFee.add(consumptionAmount).subtract(discountAmount);
    }

    private void validateRepeatedItems(CreateItemWaterPaymentRequest request) {
        Set<String> uniqueItems = request.items()
                .stream()
                .map(item -> item.billId() + "-" + item.itemType() + "-" + item.billPenaltyId())
                .collect(Collectors.toSet());

        if (uniqueItems.size() != request.items().size()) {
            throw new BadRequestException("No puede repetir el mismo item de cobro en la solicitud");
        }
    }

    private WaterPaymentDetailResponse toDetailResponse(WaterPaymentDetailEntity detail) {
        return new WaterPaymentDetailResponse(
                detail.getPaymentDetailId(),
                detail.getBillId(),
                detail.getBillPenaltyId(),
                detail.getItemType().name(),
                detail.getDescription(),
                detail.getPaymentAmount()
        );
    }

    private record PendingItemData(
            String description,
            BigDecimal amount,
            BigDecimal paidAmount,
            BigDecimal pendingAmount
    ) {
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
