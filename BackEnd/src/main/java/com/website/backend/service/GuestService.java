package com.website.backend.service;

import java.util.Map;

public interface GuestService {

	String generateGuestUsername();

	void saveGuestToRedis(String username, String password);

	Map<String, Object> getGuestFromRedis(String username);

	boolean existsInRedis(String username);

	String generateGuestToken(String username);

}