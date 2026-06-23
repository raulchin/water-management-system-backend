package com.sigap.readings.repository;

import com.sigap.readings.entity.MeterReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeterReadingRepository extends JpaRepository<MeterReadingEntity, Long> {

    boolean existsByMeterIdAndPeriod(Long meterId, String period);

    boolean existsByMeterIdAndPeriodAndReadingIdNot(Long meterId, String period, Long readingId);

    List<MeterReadingEntity> findByMeterId(Long meterId);
}
