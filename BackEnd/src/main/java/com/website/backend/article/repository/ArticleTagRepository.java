package com.website.backend.article.repository;

import com.website.backend.article.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {

	// 根据文章ID查询标签关联
	List<ArticleTag> findByArticleId(Long articleId);

	// 根据标签ID查询文章关联
	List<ArticleTag> findByTagId(Long tagId);

	// 删除文章与标签的关联
	void deleteByArticleIdAndTagId(Long articleId, Long tagId);

	// 检查文章与标签是否关联
	boolean existsByArticleIdAndTagId(Long articleId, Long tagId);

}