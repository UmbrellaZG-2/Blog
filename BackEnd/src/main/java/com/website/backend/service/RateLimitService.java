package com.website.backend.service;

public interface RateLimitService {

	boolean isIpBlocked(String ip);

	boolean recordDownloadRequest(String ip);

	void blockIp(String ip);

}