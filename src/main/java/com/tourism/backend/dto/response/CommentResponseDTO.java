package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Integer commentId;
    private String content;
    private Integer likeCount;
    private Boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private Boolean isEdited;
    private AuthorInfoDTO author;
    private List<CommentResponseDTO> replies;
}
