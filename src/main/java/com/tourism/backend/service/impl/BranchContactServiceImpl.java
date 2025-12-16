package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.BranchContactRequest;
import com.tourism.backend.dto.response.BranchContactResponse;
import com.tourism.backend.entity.BranchContact;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceInUseException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.BranchContactRepository;
import com.tourism.backend.repository.PolicyTemplateRepository;
import com.tourism.backend.service.BranchContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchContactServiceImpl implements BranchContactService {

    private final BranchContactRepository branchRepository;
    private final PolicyTemplateRepository policyRepository;

    @Override
    @Transactional
    public BranchContactResponse createBranch(BranchContactRequest request) {
        if (branchRepository.existsByBranchName(request.getBranchName())) {
            throw new DuplicateResourceException("Chi nhánh '" + request.getBranchName() + "' đã tồn tại");
        }

        if (Boolean.TRUE.equals(request.getIsHeadOffice()) && branchRepository.existsByIsHeadOfficeTrue()) {
            throw new DuplicateResourceException("Đã tồn tại trụ sở chính. Vui lòng bỏ chọn hoặc cập nhật chi nhánh cũ trước.");
        }

        BranchContact branch = mapToEntity(request);
        BranchContact savedBranch = branchRepository.save(branch);
        return mapToResponse(savedBranch);
    }

    @Override
    @Transactional
    public BranchContactResponse updateBranch(Integer id, BranchContactRequest request) {
        BranchContact branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));

        if (branchRepository.existsByBranchNameAndContactIDNot(request.getBranchName(), id)) {
            throw new DuplicateResourceException("Chi nhánh '" + request.getBranchName() + "' đã tồn tại");
        }

        if (Boolean.TRUE.equals(request.getIsHeadOffice()) && branchRepository.existsByIsHeadOfficeTrueAndContactIDNot(id)) {
            throw new DuplicateResourceException("Đã tồn tại trụ sở chính khác.");
        }

        branch.setBranchName(request.getBranchName());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setIsHeadOffice(request.getIsHeadOffice());

        BranchContact updatedBranch = branchRepository.save(branch);
        return mapToResponse(updatedBranch);
    }

    @Override
    @Transactional
    public void deleteBranch(Integer id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with id: " + id);
        }

        long policyCount = policyRepository.countByContact_ContactID(id);
        if (policyCount > 0) {
            throw new ResourceInUseException("Không thể xóa chi nhánh này vì đang được sử dụng bởi " + policyCount + " chính sách mẫu.");
        }

        branchRepository.deleteById(id);
    }

    @Override
    public BranchContactResponse getBranchById(Integer id) {
        BranchContact branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        return mapToResponse(branch);
    }

    @Override
    public Page<BranchContactResponse> getAllBranches(Pageable pageable) {
        return branchRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<BranchContactResponse> searchBranches(String keyword, Pageable pageable) {
        return branchRepository.searchBranches(keyword, pageable).map(this::mapToResponse);
    }

    @Override
    public List<BranchContactResponse> getAllBranchesSimple() {
        return branchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BranchContact mapToEntity(BranchContactRequest req) {
        BranchContact branch = new BranchContact();
        branch.setBranchName(req.getBranchName());
        branch.setPhone(req.getPhone());
        branch.setEmail(req.getEmail());
        branch.setAddress(req.getAddress());
        branch.setIsHeadOffice(req.getIsHeadOffice() != null ? req.getIsHeadOffice() : false);
        return branch;
    }

    private BranchContactResponse mapToResponse(BranchContact entity) {
        int policyCount = entity.getPolicies() != null ? entity.getPolicies().size() : 0;

        return BranchContactResponse.builder()
                .contactID(entity.getContactID())
                .branchName(entity.getBranchName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .isHeadOffice(entity.getIsHeadOffice())
                .policyCount(policyCount)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}