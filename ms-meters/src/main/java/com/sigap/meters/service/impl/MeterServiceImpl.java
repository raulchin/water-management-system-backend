package com.sigap.meters.service.impl;

import com.sigap.meters.dto.CreateMeterRequest;
import com.sigap.meters.dto.MeterResponse;
import com.sigap.meters.entity.MeterEntity;
import com.sigap.meters.enums.MeterStatus;
import com.sigap.meters.exception.DuplicateResourceException;
import com.sigap.meters.repository.MeterRepository;
import com.sigap.meters.service.MeterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;

    @Override
    @Transactional
    public MeterResponse registerMeter(CreateMeterRequest request) {

        String numeroMedidor = normalize(request.numeroMedidor());
        if (meterRepository.existsByNumeroMedidorIgnoreCase(numeroMedidor)) {
            throw new DuplicateResourceException(
                    "Ya existe un medidor registrado con el número: " + numeroMedidor
            );
        }

        MeterEntity entity = new MeterEntity();
        entity.setNumeroMedidor(numeroMedidor);
        entity.setMarca(normalize(request.marca()));
        entity.setModelo(normalize(request.modelo()));
        entity.setUbicacion(normalize(request.ubicacion()));
        entity.setDireccionReferencia(normalize(request.direccionReferencia()));
        entity.setFechaInstalacion(request.fechaInstalacion());
        entity.setEstado(MeterStatus.ACTIVO);
        entity.setObservacion(normalize(request.observacion()));

        MeterEntity saved = meterRepository.save(entity);

        return toResponse(saved);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private MeterResponse toResponse(MeterEntity entity) {
        return new MeterResponse(
                entity.getMedidorId(),
                entity.getNumeroMedidor(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getUbicacion(),
                entity.getDireccionReferencia(),
                entity.getFechaInstalacion(),
                entity.getEstado().name(),
                entity.getObservacion(),
                entity.getFechaCreacion()
        );
    }

}
