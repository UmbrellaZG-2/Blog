package com.website.backend.service.impl;

import com.website.backend.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitServiceImpl implements RateLimitService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String DOWNLOAD_RATE_LIMIT_PREFIX = "download:rate:limit:";

	private static final String BLACKLIST_PREFIX = "download:block:";

	private static final int TIME_WINDOW_SECONDS = 10;

	private static final int MAX_REQUESTS = 5;

	private static final long BLOCK_DURATION_DAYS = 1;

	@Autowired
	public RateLimitServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public boolean isIpBlocked(String ip) {
		String key = BLACKLIST_PREFIX + ip;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	@Override
	public boolean recordDownloadRequest(String ip) {
		if (isIpBlocked(ip)) {
			return true;
		}

		String key = DOWNLOAD_RATE_LIMIT_PREFIX + ip;

		Long count = redisTemplate.opsForValue().increment(key);

		if (count != null && count == 1) {
			redisTemplate.expire(key, TIME_WINDOW_SECONDS, TimeUnit.SECONDS);
		}

		if (count != null && count > MAX_REQUESTS) {
			blockIp(ip);
			return true;
		}

		return false;
	}

	@Override
	public void blockIp(String ip) {
		String key = BLACKLIST_PREFIX + ip;
		redisTemplate.opsForValue().set(key, true);
		redisTemplate.expire(key, BLOCK_DURATION_DAYS, TimeUnit.DAYS);
	}

}