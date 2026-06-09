package com.sigap.meters.service;

import com.sigap.meters.dto.CreateMeterRequest;
import com.sigap.meters.dto.MeterResponse;

public interface MeterService {

    MeterResponse registerMeter(CreateMeterRequest request);
}
