package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tourism.backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
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

    @NotBlank(message = "Phone number is required")
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

    @NotBlank(message = "Avatar is required")
    private String avatar;

    private Boolean status = true;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Column(name = "coin_balance", nullable = false)
    @Min(value = 0, message = "Coin balance cannot be negative")
    private BigDecimal coinBalance = BigDecimal.ZERO;

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