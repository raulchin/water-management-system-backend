package com.sigap.meters.service.impl;

import com.sigap.meters.client.PartnerClient;
import com.sigap.meters.dto.*;
import com.sigap.meters.entity.MeterEntity;
import com.sigap.meters.entity.PartnerMeterEntity;
import com.sigap.meters.exception.BadRequestException;
import com.sigap.meters.exception.ResourceNotFoundException;
import com.sigap.meters.repository.MeterRepository;
import com.sigap.meters.repository.PartnerMeterRepository;
import com.sigap.meters.service.PartnerMeterService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerMeterServiceImpl implements PartnerMeterService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_RETIRADO = "RETIRADO";

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "ACTIVO",
            "INACTIVO",
            "RETIRADO",
            "DANADO",
            "SUSPENDIDO"
    );

    private final PartnerMeterRepository partnerMeterRepository;

    private final MeterRepository meterRepository;

    private final PartnerClient partnerClient;

    @Override
    @Transactional
    public PartnerMeterResponse create(CreatePartnerMeterRequest request) {

        log.info("Asignar un medidor a un socio...");
        try {
            log.info("Validar si el socio existe: {}",request.socioId());
            PartnerApiResponse<PartnerResponse> partnerResponse = partnerClient.findById(request.socioId());
            PartnerResponse socio = partnerResponse.data();
            if (socio == null) {
                log.warn("No existe el socio...");
                throw new ResourceNotFoundException("No existe el socio con id: " + request.socioId());
            }
            if (!Boolean.TRUE.equals(socio.status())) {
                log.warn("Socio existe pero no esta activo...");
                throw new BadRequestException("El socio no se encuentra activo");
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el socio con id: " + request.socioId());
        }

        MeterEntity medidor = meterRepository.findById(request.medidorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el medidor con id: " + request.medidorId()
                ));

        if (partnerMeterRepository.existsByMedidor_MedidorIdAndEstado(request.medidorId(), ESTADO_ACTIVO)) {
            throw new BadRequestException("El medidor ya tiene una asignación activa");
        }

        PartnerMeterEntity entity = PartnerMeterEntity.builder()
                .medidor(medidor)
                .socioId(request.socioId())
                .fechaAsignacion(
                        request.fechaAsignacion() == null
                                ? LocalDate.now()
                                : request.fechaAsignacion()
                )
                .estado(ESTADO_ACTIVO)
                .observacion(normalize(request.observacion()))
                .build();

        PartnerMeterEntity saved = partnerMeterRepository.save(entity);

        return toResponse(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public PartnerMeterResponse findById(Long asignacionId) {

        PartnerMeterEntity entity = getAssignment(asignacionId);
        return toResponse(entity);

    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerMeterResponse> findAll() {

        return partnerMeterRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerMeterResponse> findByPartnerId(Long socioId) {

        return partnerMeterRepository.findBySocioId(socioId)
                .stream()
                .map(this::toResponse)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerMeterResponse> findByMeterId(Long medidorId) {

        return partnerMeterRepository.findByMedidor_MedidorId(medidorId)
                .stream()
                .map(this::toResponse)
                .toList();

    }

    @Override
    @Transactional
    public PartnerMeterResponse update(Long asignacionId, UpdatePartnerMeterRequest request) {

        PartnerMeterEntity entity = getAssignment(asignacionId);

        if (request.fechaAsignacion() != null) {
            entity.setFechaAsignacion(request.fechaAsignacion());
        }

        if (request.fechaRetiro() != null) {
            validateFechaRetiro(entity.getFechaAsignacion(), request.fechaRetiro());
            entity.setFechaRetiro(request.fechaRetiro());
        }

        if (request.estado() != null && !request.estado().isBlank()) {
            String estado = normalizeStatus(request.estado());
            entity.setEstado(estado);

            if (ESTADO_RETIRADO.equals(estado) && entity.getFechaRetiro() == null) {
                entity.setFechaRetiro(LocalDate.now());
            }
        }

        if (request.observacion() != null) {
            entity.setObservacion(normalize(request.observacion()));
        }

        PartnerMeterEntity updated = partnerMeterRepository.save(entity);

        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long asignacionId) {

        PartnerMeterEntity entity = getAssignment(asignacionId);

        entity.setEstado(ESTADO_RETIRADO);
        entity.setFechaRetiro(LocalDate.now());

        partnerMeterRepository.save(entity);

    }

    private PartnerMeterEntity getAssignment(Long asignacionId) {
        return partnerMeterRepository.findById(asignacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la asignación con id: " + asignacionId
                ));
    }

    private PartnerMeterResponse toResponse(PartnerMeterEntity entity) {
        return new PartnerMeterResponse(
                entity.getAsignacionId(),
                entity.getMedidor().getMedidorId(),
                entity.getMedidor().getNumeroMedidor(),
                entity.getSocioId(),
                entity.getFechaAsignacion(),
                entity.getFechaRetiro(),
                entity.getEstado(),
                entity.getObservacion(),
                entity.getFechaActualizacion()
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeStatus(String status) {
        String estado = status.trim().toUpperCase(Locale.ROOT);

        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException(
                    "Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS
            );
        }

        return estado;
    }

    private void validateFechaRetiro(LocalDate fechaAsignacion, LocalDate fechaRetiro) {
        if (fechaAsignacion != null && fechaRetiro.isBefore(fechaAsignacion)) {
            throw new BadRequestException("La fecha de retiro no puede ser menor a la fecha de asignación");
        }
    }

}
