package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.PolicyTemplateRequest;
import com.tourism.backend.dto.response.PolicyTemplateResponse;
import com.tourism.backend.entity.BranchContact;
import com.tourism.backend.entity.PolicyTemplate;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.BranchContactRepository;
import com.tourism.backend.repository.PolicyTemplateRepository;
import com.tourism.backend.service.PolicyTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyTemplateServiceImpl implements PolicyTemplateService {

    private final PolicyTemplateRepository policyRepository;
    private final BranchContactRepository branchRepository;

    @Override
    @Transactional
    public PolicyTemplateResponse createPolicy(PolicyTemplateRequest request) {
        if (policyRepository.existsByTemplateName(request.getTemplateName())) {
            throw new DuplicateResourceException("Tên mẫu chính sách '" + request.getTemplateName() + "' đã tồn tại");
        }

        BranchContact contact = branchRepository.findById(request.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getContactId()));

        PolicyTemplate policy = mapToEntity(request);
        policy.setContact(contact);

        PolicyTemplate savedPolicy = policyRepository.save(policy);
        return mapToResponse(savedPolicy);
    }

    @Override
    @Transactional
    public PolicyTemplateResponse updatePolicy(Integer id, PolicyTemplateRequest request) {
        PolicyTemplate policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));

        if (policyRepository.existsByTemplateNameAndPolicyTemplateIDNot(request.getTemplateName(), id)) {
            throw new DuplicateResourceException("Tên mẫu chính sách '" + request.getTemplateName() + "' đã tồn tại");
        }

        if (!policy.getContact().getContactID().equals(request.getContactId())) {
            BranchContact newContact = branchRepository.findById(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getContactId()));
            policy.setContact(newContact);
        }

        policy.setTemplateName(request.getTemplateName());
        policy.setTourPriceIncludes(request.getTourPriceIncludes());
        policy.setTourPriceExcludes(request.getTourPriceExcludes());
        policy.setChildPricingNotes(request.getChildPricingNotes());
        policy.setPaymentConditions(request.getPaymentConditions());
        policy.setRegistrationConditions(request.getRegistrationConditions());
        policy.setRegularDayCancellationRules(request.getRegularDayCancellationRules());
        policy.setHolidayCancellationRules(request.getHolidayCancellationRules());
        policy.setForceMajeureRules(request.getForceMajeureRules());
        policy.setPackingList(request.getPackingList());

        PolicyTemplate updatedPolicy = policyRepository.save(policy);
        return mapToResponse(updatedPolicy);
    }

    @Override
    @Transactional
    public void deletePolicy(Integer id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    @Override
    public PolicyTemplateResponse getPolicyById(Integer id) {
        PolicyTemplate policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return mapToResponse(policy);
    }

    @Override
    public Page<PolicyTemplateResponse> getAllPolicies(Pageable pageable) {
        return policyRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<PolicyTemplateResponse> searchPolicies(String keyword, Pageable pageable) {
        return policyRepository.searchPolicies(keyword, pageable).map(this::mapToResponse);
    }

    @Override
    public List<PolicyTemplateResponse> getPoliciesByContactId(Integer contactId) {
        return policyRepository.findByContact_ContactID(contactId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PolicyTemplate mapToEntity(PolicyTemplateRequest req) {
        PolicyTemplate p = new PolicyTemplate();
        p.setTemplateName(req.getTemplateName());
        p.setTourPriceIncludes(req.getTourPriceIncludes());
        p.setTourPriceExcludes(req.getTourPriceExcludes());
        p.setChildPricingNotes(req.getChildPricingNotes());
        p.setPaymentConditions(req.getPaymentConditions());
        p.setRegistrationConditions(req.getRegistrationConditions());
        p.setRegularDayCancellationRules(req.getRegularDayCancellationRules());
        p.setHolidayCancellationRules(req.getHolidayCancellationRules());
        p.setForceMajeureRules(req.getForceMajeureRules());
        p.setPackingList(req.getPackingList());
        return p;
    }

    private PolicyTemplateResponse mapToResponse(PolicyTemplate entity) {
        PolicyTemplateResponse.BranchInfo branchInfo = null;
        if (entity.getContact() != null) {
            branchInfo = new PolicyTemplateResponse.BranchInfo(
                    entity.getContact().getContactID(),
                    entity.getContact().getBranchName(),
                    entity.getContact().getPhone(),
                    entity.getContact().getEmail()
            );
        }

        int usageCount = entity.getTourDepartures() != null ? entity.getTourDepartures().size() : 0;

        return PolicyTemplateResponse.builder()
                .policyTemplateID(entity.getPolicyTemplateID())
                .templateName(entity.getTemplateName())
                .tourPriceIncludes(entity.getTourPriceIncludes())
                .tourPriceExcludes(entity.getTourPriceExcludes())
                .childPricingNotes(entity.getChildPricingNotes())
                .paymentConditions(entity.getPaymentConditions())
                .registrationConditions(entity.getRegistrationConditions())
                .regularDayCancellationRules(entity.getRegularDayCancellationRules())
                .holidayCancellationRules(entity.getHolidayCancellationRules())
                .forceMajeureRules(entity.getForceMajeureRules())
                .packingList(entity.getPackingList())
                .branchInfo(branchInfo)
                .usageCount(usageCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}