package com.tourism.backend.dto.response;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatistics {
    private Double averageRating;
    private Integer totalReviews;
    private Integer fiveStars;
    private Integer fourStars;
    private Integer threeStars;
    private Integer twoStars;
    private Integer oneStar;

    private Double fiveStarsPercent;
    private Double fourStarsPercent;
    private Double threeStarsPercent;
    private Double twoStarsPercent;
    private Double oneStarPercent;
}

