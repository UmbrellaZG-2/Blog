package com.website.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.website.backend.user.entity.Role;
import java.util.Optional;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);
	// 继承自JpaRepository的方法已包含findById等CRUD操作

}
