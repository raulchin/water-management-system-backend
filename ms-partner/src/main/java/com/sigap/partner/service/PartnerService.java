package com.sigap.partner.service;

import com.sigap.partner.dto.partner.PartnerCreateRequest;
import com.sigap.partner.dto.partner.PartnerResponse;
import com.sigap.partner.dto.partner.PartnerUpdateRequest;

import java.util.List;

public interface PartnerService {

    PartnerResponse create(PartnerCreateRequest request);

    List<PartnerResponse> findAll();

    PartnerResponse findById(Long partnerId);

    PartnerResponse update(Long partnerId, PartnerUpdateRequest request);

    void delete(Long partnerId);
}
