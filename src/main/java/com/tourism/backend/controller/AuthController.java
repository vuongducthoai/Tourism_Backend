package com.tourism.backend.controller;

import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) throws BadRequestException {
        RegisterResponseDTO response = userService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
