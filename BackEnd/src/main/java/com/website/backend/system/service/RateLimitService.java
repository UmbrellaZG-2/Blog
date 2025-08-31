package com.website.backend.system.service;

public interface RateLimitService {

	boolean isIpBlocked(String ip);

	boolean recordDownloadRequest(String ip);

	void blockIp(String ip);

}
