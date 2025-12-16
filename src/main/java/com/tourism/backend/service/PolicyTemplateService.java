package com.tourism.backend.service;

import com.tourism.backend.dto.request.PolicyTemplateRequest;
import com.tourism.backend.dto.response.PolicyTemplateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PolicyTemplateService {
    PolicyTemplateResponse createPolicy(PolicyTemplateRequest request);
    PolicyTemplateResponse updatePolicy(Integer id, PolicyTemplateRequest request);
    void deletePolicy(Integer id);
    PolicyTemplateResponse getPolicyById(Integer id);
    Page<PolicyTemplateResponse> getAllPolicies(Pageable pageable);
    Page<PolicyTemplateResponse> searchPolicies(String keyword, Pageable pageable);
    List<PolicyTemplateResponse> getPoliciesByContactId(Integer contactId);
}