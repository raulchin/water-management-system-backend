package com.sigap.readings.event;

import com.sigap.readings.dto.PartnerMeterResponse;
import com.sigap.readings.entity.MeterReadingEntity;

public record MeterReadingUpdatedEvent(

        MeterReadingEntity reading,
        PartnerMeterResponse assignment

) {
}
