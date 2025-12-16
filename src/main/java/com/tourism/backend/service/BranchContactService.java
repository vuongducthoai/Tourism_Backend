package com.tourism.backend.service;

import com.tourism.backend.dto.request.BranchContactRequest;
import com.tourism.backend.dto.response.BranchContactResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BranchContactService {
    BranchContactResponse createBranch(BranchContactRequest request);
    BranchContactResponse updateBranch(Integer id, BranchContactRequest request);
    void deleteBranch(Integer id);
    BranchContactResponse getBranchById(Integer id);
    Page<BranchContactResponse> getAllBranches(Pageable pageable);
    Page<BranchContactResponse> searchBranches(String keyword, Pageable pageable);
    List<BranchContactResponse> getAllBranchesSimple();
}