package com.sigap.readings.service;

import com.sigap.readings.dto.*;

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

    PreviousMeterReadingResponse findPreviousByMeterIdAndPeriod(Long meterId, String period);

    PageResponse<MeterReadingResponse> findAllPaged(int page, int size);
}
