package com.sigap.readings.dto;

import java.util.List;

public record PartnerAssignmentsResponse(

        PartnerSummaryResponse socio,
        List<MeterAssignmentSummaryResponse> asignaciones

) {
}
