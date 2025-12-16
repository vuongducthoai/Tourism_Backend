package com.tourism.backend.controller;

import com.tourism.backend.dto.request.PolicyTemplateRequest;
import com.tourism.backend.dto.response.PolicyTemplateResponse;
import com.tourism.backend.service.PolicyTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policy-templates")
@RequiredArgsConstructor
public class PolicyTemplateController {

    private final PolicyTemplateService policyTemplateService;

    @PostMapping
    public ResponseEntity<PolicyTemplateResponse> createPolicy(@Valid @RequestBody PolicyTemplateRequest request) {
        PolicyTemplateResponse response = policyTemplateService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{policyTemplateID}")
    public ResponseEntity<PolicyTemplateResponse> updatePolicy(
            @PathVariable Integer policyTemplateID,
            @Valid @RequestBody PolicyTemplateRequest request) {
        PolicyTemplateResponse response = policyTemplateService.updatePolicy(policyTemplateID, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{policyTemplateID}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Integer policyTemplateID) {
        policyTemplateService.deletePolicy(policyTemplateID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{policyTemplateID}")
    public ResponseEntity<PolicyTemplateResponse> getPolicyById(@PathVariable Integer policyTemplateID) {
        return ResponseEntity.ok(policyTemplateService.getPolicyById(policyTemplateID));
    }

    @GetMapping
    public ResponseEntity<Page<PolicyTemplateResponse>> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(policyTemplateService.getAllPolicies(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PolicyTemplateResponse>> searchPolicies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(policyTemplateService.searchPolicies(keyword, pageable));
    }

    @GetMapping("/by-contact/{contactId}")
    public ResponseEntity<List<PolicyTemplateResponse>> getPoliciesByContactId(@PathVariable Integer contactId) {
        return ResponseEntity.ok(policyTemplateService.getPoliciesByContactId(contactId));
    }
}