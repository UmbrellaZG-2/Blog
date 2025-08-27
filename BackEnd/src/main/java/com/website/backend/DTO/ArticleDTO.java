package com.website.backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleDTO {

	private Long id;

	private String title;

	private String content;

	private String category;

	private boolean hasAttachment;

	private boolean hasCoverImage;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}