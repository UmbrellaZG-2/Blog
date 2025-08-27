package com.website.backend.repository.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.website.backend.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

	// 按分类查询文章
	Page<Article> findByCategory(String category, Pageable pageable);

	// 分页查询所有文章
	@Override
	Page<Article> findAll(Pageable pageable);

	// 根据ID列表查询文章
	Page<Article> findByIdIn(List<Long> ids, Pageable pageable);

	// 根据文章标题模糊搜索
	Page<Article> findByTitleContaining(String title, Pageable pageable);

	// 获取所有不同的分类
	@Query("SELECT DISTINCT a.category FROM Article a")
	List<String> findDistinctCategories();

	// 根据articleId查询文章
	Optional<Article> findByArticleId(UUID articleId);
	
	// 添加缺失的搜索方法
	Page<Article> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
	
	Page<Article> findByCategoryAndTitleContainingOrContentContaining(String category, String title, String content, Pageable pageable);

}