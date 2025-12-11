package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserResponseDTO {
    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatar;
    private BigDecimal coinBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    public UserResponseDTO(User user) {
        this.id = user.getUserID();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhone();
        this.dateOfBirth = user.getDateOfBirth();
        this.avatar = user.getAvatar();
        this.coinBalance = user.getCoinBalance();

    }
}