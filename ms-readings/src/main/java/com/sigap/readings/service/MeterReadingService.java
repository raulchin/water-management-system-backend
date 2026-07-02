package com.sigap.readings.service;

import com.sigap.readings.dto.CreateMeterReadingRequest;
import com.sigap.readings.dto.MeterReadingResponse;
import com.sigap.readings.dto.MeterReadingSearchResponse;
import com.sigap.readings.dto.UpdateMeterReadingRequest;

import java.util.List;

public interface MeterReadingService {

    MeterReadingResponse create(CreateMeterReadingRequest request);

    List<MeterReadingResponse> findAll();

    List<MeterReadingResponse> findByMeterId(Long meterId);

    MeterReadingResponse findById(Long readingId);

    MeterReadingResponse update(Long readingId, UpdateMeterReadingRequest request);

    void delete(Long readingId);

    List<MeterReadingSearchResponse> searchByIdentificationOrMeterNumber(
            String identification,
            String meterNumber,
            String period
    );
}
