package com.website.backend.user.service;

import com.website.backend.user.entity.User;
import java.util.Optional;

public interface UserService {

	User registerUser(String username, String password);
	User registerUser(String username, String password, boolean isAdmin);

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

}
