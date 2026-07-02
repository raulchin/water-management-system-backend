package com.sigap.readings.repository;

import com.sigap.readings.entity.MeterReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReadingEntity, Long> {

    boolean existsByMeterIdAndPeriod(Long meterId, String period);

    boolean existsByMeterIdAndPeriodAndReadingIdNot(Long meterId, String period, Long readingId);

    List<MeterReadingEntity> findByMeterId(Long meterId);

    Optional<MeterReadingEntity> findByMeterIdAndPeriod(Long meterId, String period);

    List<MeterReadingEntity> findByAssignmentIdInAndPeriod(Collection<Long> assignmentIds, String period);
}
