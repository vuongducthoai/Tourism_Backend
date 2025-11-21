package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "branch_contacts")
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchContact  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactID;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Column(name = "departure_id")
    private Integer departureID;
}