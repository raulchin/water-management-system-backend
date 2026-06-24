package com.sigap.meters.service;

import com.sigap.meters.dto.*;

import java.util.List;

public interface PartnerMeterService {


    PartnerMeterResponse create(CreatePartnerMeterRequest request);

    PartnerMeterResponse findById(Long asignacionId);

    List<PartnerMeterResponse> findAll();

    List<PartnerMeterResponse> findByPartnerId(Long socioId);

    List<PartnerMeterResponse> findByMeterId(Long medidorId);

    PartnerMeterResponse update(Long asignacionId, UpdatePartnerMeterRequest request);

    void delete(Long asignacionId);

    PartnerAssignmentsResponse findByPartnerIdentification(String identification);
}
