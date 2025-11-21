package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "policy_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class PolicyTemplate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer policyTemplateID;

    @Column(columnDefinition = "TEXT")
    private String childPricingNotes;

    @Column(columnDefinition = "TEXT")
    private String paymentConditions;

    @Column(columnDefinition = "TEXT")
    private String registrationConditions;

    @Column(columnDefinition = "TEXT")
    private String regularDayCancellationRules;

    @Column(columnDefinition = "TEXT")
    private String holidayCancellationRules;

    @Column(columnDefinition = "TEXT")
    private String forceMajeureRules;

    @Column(columnDefinition = "TEXT")
    private String packingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private BranchContact contact;
}