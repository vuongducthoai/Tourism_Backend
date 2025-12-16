package com.tourism.backend.controller;

import com.tourism.backend.dto.request.BranchContactRequest;
import com.tourism.backend.dto.response.BranchContactResponse;
import com.tourism.backend.service.BranchContactService;
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
@RequestMapping("/api/admin/branches")
@RequiredArgsConstructor
public class BranchContactController {

    private final BranchContactService branchContactService;

    @PostMapping
    public ResponseEntity<BranchContactResponse> createBranch(@Valid @RequestBody BranchContactRequest request) {
        BranchContactResponse response = branchContactService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{contactID}")
    public ResponseEntity<BranchContactResponse> updateBranch(
            @PathVariable Integer contactID,
            @Valid @RequestBody BranchContactRequest request) {
        BranchContactResponse response = branchContactService.updateBranch(contactID, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{contactID}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Integer contactID) {
        branchContactService.deleteBranch(contactID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{contactID}")
    public ResponseEntity<BranchContactResponse> getBranchById(@PathVariable Integer contactID) {
        return ResponseEntity.ok(branchContactService.getBranchById(contactID));
    }

    @GetMapping
    public ResponseEntity<Page<BranchContactResponse>> getAllBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(branchContactService.getAllBranches(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BranchContactResponse>> searchBranches(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(branchContactService.searchBranches(keyword, pageable));
    }

    @GetMapping("/simple")
    public ResponseEntity<List<BranchContactResponse>> getAllBranchesSimple() {
        return ResponseEntity.ok(branchContactService.getAllBranchesSimple());
    }
}