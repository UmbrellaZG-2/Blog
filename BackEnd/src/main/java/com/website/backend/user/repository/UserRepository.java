package com.website.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.website.backend.user.entity.User;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
	// 继承自JpaRepository的方法已包含findById等CRUD操作

}
