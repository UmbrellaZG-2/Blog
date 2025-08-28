package com.website.backend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.website.backend.comment.entity.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByArticleId(Long articleId);

	List<Comment> findByParentId(Long parentId);
	
	List<Comment> findByArticleIdAndParentIdIsNull(Long articleId);
	
	List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);
	
	List<Comment> findByArticleIdOrderByCreateTimeDesc(Long articleId);
	
	List<Comment> findByParentIdOrderByCreateTimeAsc(Long parentId);

}
