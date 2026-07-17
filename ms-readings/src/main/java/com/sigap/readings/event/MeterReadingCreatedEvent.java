package com.sigap.readings.event;

import com.sigap.readings.dto.PartnerMeterResponse;
import com.sigap.readings.entity.MeterReadingEntity;

public record MeterReadingCreatedEvent(

        MeterReadingEntity reading,
        PartnerMeterResponse assignment
) {
}
