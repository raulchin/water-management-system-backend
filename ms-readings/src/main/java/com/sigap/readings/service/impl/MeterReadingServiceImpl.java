package com.sigap.readings.service.impl;

import com.sigap.readings.client.MeterClient;
import com.sigap.readings.dto.*;
import com.sigap.readings.entity.MeterReadingEntity;
import com.sigap.readings.enums.MeterReadingStatus;
import com.sigap.readings.exception.BadRequestException;
import com.sigap.readings.exception.DuplicateResourceException;
import com.sigap.readings.exception.ResourceNotFoundException;
import com.sigap.readings.repository.MeterReadingRepository;
import com.sigap.readings.service.MeterReadingService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;

    private final MeterClient meterClient;

    @Override
    @Transactional
    public MeterReadingResponse create(CreateMeterReadingRequest request) {

        String period = normalize(request.period());
        validateAssignment(request);
        validateReadingValues(request.previousReading(), request.currentReading());
        validatePeriodAvailability(request.meterId(), period, null);

        MeterReadingEntity entity = new MeterReadingEntity();
        entity.setMeterId(request.meterId());
        entity.setAssignmentId(request.assignmentId());
        entity.setPartnerId(request.partnerId());
        entity.setPeriod(period);
        entity.setReadingDate(request.readingDate());
        entity.setPreviousReading(request.previousReading());
        entity.setCurrentReading(request.currentReading());
        entity.setStatus(request.status() == null ? MeterReadingStatus.REGISTRADA : request.status());
        entity.setObservation(normalize(request.observation()));

        return toResponse(meterReadingRepository.save(entity));
    }

    @Override
    public List<MeterReadingResponse> findAll() {

        List<MeterReadingEntity> readings = meterReadingRepository.findAll();

        Map<Long, PartnerMeterResponse> assignments = readings.stream()
                .map(MeterReadingEntity::getAssignmentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(
                        assignmentId -> assignmentId,
                        this::findAssignment
                ));

        return readings.stream()
                .map(reading -> toResponseList(reading, assignments.get(reading.getAssignmentId())))
                .toList();

    }

    @Override
    public List<MeterReadingResponse> findByMeterId(Long meterId) {

        List<MeterReadingEntity> readings = meterReadingRepository.findByMeterId(meterId);

        Map<Long, PartnerMeterResponse> assignments = readings.stream()
                .map(MeterReadingEntity::getAssignmentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(
                        assignmentId -> assignmentId,
                        this::findAssignment
                ));

        return readings.stream()
                .map(reading -> toResponseList(reading, assignments.get(reading.getAssignmentId())))
                .toList();
    }

    @Override
    public MeterReadingResponse findById(Long readingId) {
        return toResponse(findReading(readingId));
    }

    @Override
    @Transactional
    public MeterReadingResponse update(Long readingId, UpdateMeterReadingRequest request) {
        MeterReadingEntity entity = findReading(readingId);
        String period = normalize(request.period());
        validateReadingValues(request.previousReading(), request.currentReading());
        validatePeriodAvailability(request.meterId(), period, readingId);

        entity.setMeterId(request.meterId());
        entity.setAssignmentId(request.assignmentId());
        entity.setPartnerId(request.partnerId());
        entity.setPeriod(period);
        entity.setReadingDate(request.readingDate());
        entity.setPreviousReading(request.previousReading());
        entity.setCurrentReading(request.currentReading());
        entity.setStatus(request.status());
        entity.setObservation(normalize(request.observation()));

        return toResponse(meterReadingRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(Long readingId) {
        meterReadingRepository.delete(findReading(readingId));
    }

    @Override
    public List<MeterReadingSearchResponse> searchByIdentificationOrMeterNumber(String identification, String meterNumber, String period) {

        String normalizedPeriod = normalize(period);
        String normalizedIdentification = normalize(identification);
        String normalizedMeterNumber = normalize(meterNumber);

        validateSearchCriteria(normalizedIdentification, normalizedMeterNumber, normalizedPeriod);

        if (normalizedIdentification != null) {
            return searchByIdentification(normalizedIdentification, normalizedPeriod);
        }

        return searchByMeterNumber(normalizedMeterNumber, normalizedPeriod);
    }

    private void validateSearchCriteria(String identification, String meterNumber, String period) {
        if (period == null || period.isBlank()) {
            throw new BadRequestException("El periodo es obligatorio");
        }

        boolean hasIdentification = identification != null && !identification.isBlank();
        boolean hasMeterNumber = meterNumber != null && !meterNumber.isBlank();

        if (hasIdentification == hasMeterNumber) {
            throw new BadRequestException("Debe enviar identification o meterNumber, pero no ambos");
        }
    }

    private List<MeterReadingSearchResponse> searchByIdentification(String identification, String period) {

        log.info("Buscar lecuras de medidores del socio: {}", identification);
        ApiResponse<PartnerAssignmentsResponse> response =
                meterClient.findAssignmentsByIdentification(identification);

        if (response == null || response.data() == null) {
            throw new ResourceNotFoundException("No existe socio con identificacion: " + identification);
        }

        PartnerAssignmentsResponse partnerAssignments = response.data();

        List<Long> assignmentIds = partnerAssignments.asignaciones()
                .stream()
                .map(MeterAssignmentSummaryResponse::asignacionId)
                .toList();

        if (assignmentIds.isEmpty()) {
            return List.of();
        }

        List<MeterReadingEntity> readings =
                meterReadingRepository.findByAssignmentIdInAndPeriod(assignmentIds, period);

        Map<Long, MeterAssignmentSummaryResponse> assignmentsById = partnerAssignments.asignaciones()
                .stream()
                .collect(Collectors.toMap(
                        MeterAssignmentSummaryResponse::asignacionId,
                        assignment -> assignment
                ));

        return readings.stream()
                .map(reading -> toSearchResponse(
                        reading,
                        partnerAssignments.socio(),
                        assignmentsById.get(reading.getAssignmentId())
                ))
                .toList();
    }

    private List<MeterReadingSearchResponse> searchByMeterNumber(String meterNumber, String period) {

        log.info("Busqueda de lecturas segun el numero de medidor: {}", meterNumber);
        ApiResponse<MeterResponse> response = meterClient.findMeterByNumber(meterNumber);

        if (response == null || response.data() == null) {
            throw new ResourceNotFoundException("No existe medidor con numero: " + meterNumber);
        }

        MeterResponse meter = response.data();

        MeterReadingEntity reading = meterReadingRepository.findByMeterIdAndPeriod(meter.medidorId(), period)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe lectura para el medidor " + meterNumber + " en el periodo " + period
                ));

        PartnerMeterResponse assignment = findAssignment(reading.getAssignmentId());

        PartnerSummaryResponse partner = new PartnerSummaryResponse(
                assignment.socioId(),
                assignment.identificacionSocio(),
                null,
                null
        );

        MeterAssignmentSummaryResponse assignmentSummary = new MeterAssignmentSummaryResponse(
                assignment.asignacionId(),
                assignment.medidorId(),
                assignment.numeroMedidor(),
                assignment.marcaMedidor(),
                assignment.modeloMedidor(),
                assignment.estado()
        );

        return List.of(toSearchResponse(reading, partner, assignmentSummary));
    }


    private MeterReadingSearchResponse toSearchResponse(
            MeterReadingEntity reading,
            PartnerSummaryResponse partner,
            MeterAssignmentSummaryResponse assignment
    ) {
        log.info("Construir la respuesta de lecturas...");
        return new MeterReadingSearchResponse(
                new MeterReadingSearchResponse.PartnerInfo(
                        partner.socioId(),
                        partner.identificacionSocio(),
                        partner.nombreSocio(),
                        partner.email()
                ),
                new MeterReadingSearchResponse.MeterInfo(
                        assignment.medidorId(),
                        assignment.asignacionId(),
                        assignment.numeroMedidor(),
                        assignment.marcaMedidor(),
                        assignment.modeloMedidor()
                ),
                new MeterReadingSearchResponse.ReadingInfo(
                        reading.getReadingId(),
                        reading.getPeriod(),
                        reading.getReadingDate(),
                        reading.getPreviousReading(),
                        reading.getCurrentReading(),
                        reading.getCalculatedConsumption(),
                        reading.getStatus().name(),
                        reading.getObservation()
                )
        );
    }

    private MeterReadingEntity findReading(Long readingId) {
        return meterReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Lectura de medidor no encontrada con id " + readingId));
    }

    private void validatePeriodAvailability(Long meterId, String period, Long readingId) {
        boolean exists = readingId == null
                ? meterReadingRepository.existsByMeterIdAndPeriod(meterId, period)
                : meterReadingRepository.existsByMeterIdAndPeriodAndReadingIdNot(meterId, period, readingId);

        if (exists) {
            throw new DuplicateResourceException("Ya existe una lectura para el medidor " + meterId + " en el periodo " + period);
        }
    }

    private void validateReadingValues(BigDecimal previousReading, BigDecimal currentReading) {
        BigDecimal previous = previousReading == null ? BigDecimal.ZERO : previousReading;
        if (currentReading.compareTo(previous) < 0) {
            throw new BadRequestException("La lectura actual no puede ser menor que la lectura anterior");
        }
    }

    private PartnerMeterResponse findAssignment(Long assignmentId) {
        if (assignmentId == null) {
            return null;
        }

        ApiResponse<PartnerMeterResponse> response = meterClient.findAssignmentById(assignmentId);

        if (response == null || response.data() == null) {
            return null;
        }

        return response.data();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private MeterReadingResponse toResponse(MeterReadingEntity entity) {


        return new MeterReadingResponse(
                entity.getReadingId(),
                entity.getMeterId(),
                entity.getAssignmentId(),
                entity.getPartnerId(),
                entity.getPeriod(),
                entity.getReadingDate(),
                entity.getPreviousReading(),
                entity.getCurrentReading(),
                entity.getCalculatedConsumption(),
                entity.getStatus().name(),
                entity.getObservation(),
                entity.getCreationDate(),
                entity.getUpdateDate(),
                "",
                ""
        );
    }

    private MeterReadingResponse toResponseList(MeterReadingEntity entity, PartnerMeterResponse assignment) {

        log.info("Datos asignacion: ", assignment.toString());
        return new MeterReadingResponse(
                entity.getReadingId(),
                entity.getMeterId(),
                entity.getAssignmentId(),
                entity.getPartnerId(),
                entity.getPeriod(),
                entity.getReadingDate(),
                entity.getPreviousReading(),
                entity.getCurrentReading(),
                entity.getCalculatedConsumption(),
                entity.getStatus().name(),
                entity.getObservation(),
                entity.getCreationDate(),
                entity.getUpdateDate(),
                assignment == null ? null : assignment.numeroMedidor(),
                assignment == null ? null : assignment.identificacionSocio()
        );
    }

    private PartnerMeterResponse validateAssignment(CreateMeterReadingRequest request) {
        try {
            log.info("Validar si existe la asignacion: {}", request.assignmentId());
            ApiResponse<PartnerMeterResponse> response = meterClient.findAssignmentById(request.assignmentId());

            if (response == null || response.data() == null) {
                throw new ResourceNotFoundException(
                        "No existe la asignacio con id: " + request.assignmentId()
                );
            }

            PartnerMeterResponse assignment = response.data();

            if (!"ACTIVO".equalsIgnoreCase(assignment.estado())) {
                throw new BadRequestException(
                        "La asignacion no se encuentra activa"
                );
            }

            if (!assignment.medidorId().equals(request.meterId())) {
                throw new BadRequestException(
                        "El medidor no corresponde a la asignacion indicada"
                );
            }

            if (!assignment.socioId().equals(request.partnerId())) {
                throw new BadRequestException(
                        "El socio no corresponde a la asignacion indicada"
                );
            }

            return assignment;

        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "No existe la asignacion con id: " + request.assignmentId()
            );
        }
    }
}
