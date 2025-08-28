package com.website.backend.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.website.backend.article.entity.Tag;
import java.util.Optional;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByName(String name);
	
	@Query("SELECT t FROM Tag t JOIN ArticleTag at ON t.id = at.tagId WHERE at.articleId = ?1")
	List<Tag> findByArticleId(Long articleId);

}