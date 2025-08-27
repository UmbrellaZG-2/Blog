package com.website.backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String nickname;
    private String content;
    private LocalDateTime createTime;
    private String ipAddress;
    private List<CommentDTO> replies = new ArrayList<>();
}