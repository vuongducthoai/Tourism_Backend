package com.tourism.backend.controller;

import com.tourism.backend.dto.requestDTO.UserSearchRequestDTO;
import com.tourism.backend.dto.requestDTO.UserStatusUpdateRequestDTO;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import com.tourism.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userID}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userID) {
        try {
            UserReaponseDTO user = userService.getUserById(userID);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi lấy thông tin user: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(value = "/{userID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // ✨ CẬP NHẬT: Cho phép gửi file ✨
    public ResponseEntity<?> updateUser(
            @PathVariable Integer userID,
            @ModelAttribute UserUpdateRequestDTO updateDTO
    ) {
        try {
            System.out.println(updateDTO.getPhone());
            // Service sẽ ném IOException nếu upload Cloudinary thất bại
            UserReaponseDTO updatedUser = userService.updateUser(userID, updateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) { // Bắt lỗi IOException nếu có khi upload Cloudinary
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Upload Error",
                    "Lỗi khi upload ảnh Avatar: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi cập nhật thông tin user: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/admin/search")
    public ResponseEntity<Page<UserReaponseDTO>> searchUsers(
            @RequestBody UserSearchRequestDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(searchDTO, pageable));
    }

    @PostMapping("/admin/update-status")
    public ResponseEntity<?> updateUserStatus(@RequestBody UserStatusUpdateRequestDTO requestDTO) {
        try {
            UserReaponseDTO updatedUser = userService.updateUserStatus(requestDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(500, "Error", e.getMessage()));
        }
    }
}

