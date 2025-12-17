package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "policy_templates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PolicyTemplate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer policyTemplateID;

    @NotBlank(message = "The policy template name cannot be empty")
    @Size(max = 255)
    @Column(name = "template_name", unique = true, nullable = false)
    private String templateName;

    @Column(columnDefinition = "TEXT")
    private String tourPriceIncludes;

    @Column(columnDefinition = "TEXT")
    private String tourPriceExcludes;

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

    @OneToMany(mappedBy = "policyTemplate")
    private List<TourDeparture> tourDepartures;

    @Column(name = "status")
    private Boolean status = true;
}