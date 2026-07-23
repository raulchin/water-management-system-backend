package com.sigap.billing.service;

import com.sigap.billing.dto.penalty.CreatePenaltyTypeRequest;
import com.sigap.billing.dto.penalty.PenaltyTypeResponse;

import java.util.List;

public interface PenaltyTypeService {

    PenaltyTypeResponse create(CreatePenaltyTypeRequest request);

    List<PenaltyTypeResponse> findActive();
}
