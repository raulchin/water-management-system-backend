package com.sigap.meters.service;

import com.sigap.meters.dto.CreateMeterRequest;
import com.sigap.meters.dto.MeterResponse;

import java.util.List;

public interface MeterService {

    MeterResponse registerMeter(CreateMeterRequest request);

    List<MeterResponse> findAll();

    MeterResponse findByMeterNumber(String meterNumber);
}
