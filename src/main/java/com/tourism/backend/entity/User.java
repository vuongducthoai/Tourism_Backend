package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tourism.backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"bookings", "favoriteTours", "reviews"})
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @Pattern(regexp = "^\\d{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    private Boolean status = true;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Column(name = "coin_balance")
    @Min(value = 0, message = "Coin balance cannot be negative")
    private BigDecimal coinBalance = BigDecimal.ZERO;

    @Column(name = "province_code", length = 10)
    private String provinceCode;

    @Column(name = "province_name", length = 100)
    private String provinceName;

    @Column(name = "district_code", length = 10)
    private String districtCode;

    @Column(name = "district_name", length = 100)
    private String districtName;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    private LocalDateTime lastActiveAt;

    @Transient
    public boolean isOnline() {
        if (lastActiveAt == null) {
            return false;
        }

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return lastActiveAt.isAfter(fiveMinutesAgo);
    }

    @Transient
    public String getActivityStatus() {
        if (lastActiveAt == null) {
            return "Offline";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);

        if (lastActiveAt.isAfter(fiveMinutesAgo)) {
            return "Online";
        } else if (lastActiveAt.isAfter(thirtyMinutesAgo)) {
            return "Away";
        } else {
            return "Offline";
        }
    }



    // --- Relationships ---
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<FavoriteTour> favoriteTours;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private  List<Review> reviews;
}