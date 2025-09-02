package com.website.backend.file.controller;

import com.website.backend.article.entity.Article;
import com.website.backend.common.constant.HttpStatusConstants;
import com.website.backend.file.entity.Attachment;
import com.website.backend.article.repository.ArticleRepository;
import com.website.backend.file.repository.AttachmentRepository;
import com.website.backend.file.service.AttachmentService;
import com.website.backend.system.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

	@Autowired
	private ArticleRepository articleRepo;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private RateLimitService rateLimitService;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	// 附件下载次数的Redis键前缀
	private static final String ATTACHMENT_DOWNLOAD_COUNT_PREFIX = "attachment:download:count:";

	@GetMapping("/download/{attachmentId}")
	public void downloadAttachment(@PathVariable Long attachmentId, HttpServletRequest request,
			HttpServletResponse response) {
		// 获取客户端IP地址
		String clientIp = getClientIpAddress(request);

		logger.info("开始处理附件下载请求，附件ID: {}, 客户端IP: {}", attachmentId, clientIp);
		
		// 检查IP是否被阻止或超过下载限制
		if (rateLimitService.isIpBlocked(clientIp) || rateLimitService.recordDownloadRequest(clientIp)) {
			logger.warn("客户端IP被阻止或超过下载限制: {}", clientIp);
			response.setStatus(HttpStatusConstants.FORBIDDEN);
			try {
				response.getWriter().write("下载频率过高，请24小时后再试");
			}
			catch (IOException ex) {
				logger.error("写入响应失败", ex);
			}
			return;
		}
		
		try {
			// 检查附件ID是否有效
			if (attachmentId == null) {
				logger.error("附件ID不能为空");
				response.setStatus(HttpStatusConstants.BAD_REQUEST);
				response.getWriter().write("附件ID不能为空");
				return;
			}
			
			logger.info("开始下载附件，附件ID: {}", attachmentId);
			
			// 查找附件记录
			Attachment attachment = attachmentRepository.findById(attachmentId).orElse(null);
			if (attachment == null) {
				logger.error("附件不存在，附件ID: {}", attachmentId);
				response.setStatus(HttpStatusConstants.NOT_FOUND);
				response.getWriter().write("附件不存在");
				return;
			}
			
			logger.info("找到附件记录，文件名: {}, 文件路径: {}", attachment.getFileName(), attachment.getFilePath());
			
			// 检查文件路径是否为空
			if (attachment.getFilePath() == null || attachment.getFilePath().isEmpty()) {
				logger.error("附件文件路径为空，附件ID: {}", attachmentId);
				response.setStatus(HttpStatusConstants.INTERNAL_SERVER_ERROR);
				response.getWriter().write("附件文件路径为空");
				return;
			}
			
			// 检查文件是否存在
			java.io.File file = new java.io.File(attachment.getFilePath());
			if (!file.exists()) {
				logger.error("附件文件不存在，文件路径: {}", attachment.getFilePath());
				response.setStatus(HttpStatusConstants.NOT_FOUND);
				response.getWriter().write("附件文件不存在");
				return;
			}
			
			// 下载文件内容
			byte[] fileContent = attachmentService.downloadAttachment(attachmentId);

			// 增加附件下载次数
			String downloadCountKey = ATTACHMENT_DOWNLOAD_COUNT_PREFIX + attachmentId;
			redisTemplate.opsForValue().increment(downloadCountKey, 1);
			// 设置过期时间，避免键永久存在（例如设置为30天）
			redisTemplate.expire(downloadCountKey, 30, TimeUnit.DAYS);

			// 使用原始文件名
			String fileName = attachment.getFileName();
			
			// 对文件名进行URL编码以支持中文等特殊字符
			String encodedFileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8);
			
			// 设置响应头
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
			
			// 添加调试信息头
			response.setHeader("X-Debug-Attachment-Id", String.valueOf(attachmentId));
			response.setHeader("X-Debug-Original-Filename", attachment.getFileName());
			// 暴露这些头部给前端
			response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, 
				HttpHeaders.CONTENT_DISPOSITION + ", X-Debug-Attachment-Id, X-Debug-Original-Filename");
			// 添加跨域支持
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentLength(fileContent.length);
			
			// 添加调试日志
			logger.info("下载附件信息 - ID: {}, 原始文件名: {}, Encoded文件名: {}, Content-Disposition: {}", 
			           attachmentId, attachment.getFileName(), encodedFileName, contentDisposition);
			
			// 写入响应体
			try (OutputStream os = response.getOutputStream()) {
				os.write(fileContent);
				os.flush();
			}
			logger.info("附件下载完成，附件ID: {}", attachmentId);
		}
		catch (IOException e) {
			logger.error("附件不存在或读取失败，附件ID: {}, 错误信息: {}", attachmentId, e.getMessage(), e);
			response.setStatus(HttpStatusConstants.NOT_FOUND);
			try {
				response.getWriter().write("附件不存在: " + e.getMessage());
			}
			catch (IOException ex) {
				logger.error("写入响应失败", ex);
			}
		}
		catch (Exception e) {
			logger.error("下载附件失败，附件ID: {}", attachmentId, e);
			response.setStatus(HttpStatusConstants.INTERNAL_SERVER_ERROR);
			try {
				response.getWriter().write("下载附件失败: " + e.getMessage());
			}
			catch (IOException ex) {
				logger.error("写入响应失败", ex);
			}
		}
	}

	// 获取客户端IP地址
	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");
		if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
			return xForwardedForHeader.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	@PostMapping("/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> uploadAttachment(@RequestParam("file") MultipartFile file,
			@RequestParam("articleId") Long articleId) {
		try {
			// 根据articleId查询Article对象
			Article article = articleRepo.findById(articleId)
					.orElseThrow(() -> new RuntimeException("文章不存在"));
			Attachment attachment = attachmentService.uploadAttachment(file, article);
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "附件上传成功");
			response.put("attachmentId", attachment.getAttachmentId());
			response.put("fileName", attachment.getFileName());
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			logger.error("附件上传失败: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "附件上传失败: " + e.getMessage());
			return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR).body(response);
		} catch (Exception e) {
			logger.error("附件上传异常: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "附件上传异常: " + e.getMessage());
			return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/delete/{attachmentId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, String>> deleteAttachment(@PathVariable Long attachmentId) {
		try {
			attachmentService.deleteAttachment(attachmentId);
			Map<String, String> response = new HashMap<>();
			response.put("success", "true");
			response.put("message", "附件删除成功");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("附件删除失败: {}", e.getMessage());
			Map<String, String> response = new HashMap<>();
			response.put("success", "false");
			response.put("message", "附件删除失败: " + e.getMessage());
			return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/article/get/{articleId}")
	public ResponseEntity<Map<String, Object>> getAttachmentsByArticleId(@PathVariable Long articleId) {
		try {
			// 根据articleId查询Article对象
			Article article = articleRepo.findById(articleId)
					.orElseThrow(() -> new RuntimeException("文章不存在"));
			// 使用attachmentRepository查询该文章的所有附件
			List<Attachment> attachments = attachmentRepository.findByArticle(article);
			
			// 为每个附件添加下载次数信息
			for (Attachment attachment : attachments) {
				String downloadCountKey = ATTACHMENT_DOWNLOAD_COUNT_PREFIX + attachment.getAttachmentId();
				Object countObj = redisTemplate.opsForValue().get(downloadCountKey);
				// 如果Redis中没有记录，则默认为0
				long downloadCount = 0L;
				if (countObj != null) {
					// 处理可能的Integer或Long类型
					if (countObj instanceof Integer) {
						downloadCount = ((Integer) countObj).longValue();
					} else if (countObj instanceof Long) {
						downloadCount = (Long) countObj;
					}
				}
				attachment.setDownloadCount(downloadCount);
			}
			
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "获取附件列表成功");
			response.put("attachments", attachments);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("获取附件列表失败: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "获取附件列表失败: " + e.getMessage());
			return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/get")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getAllAttachments() {
		try {
			// 使用attachmentRepository查询所有附件
			List<Attachment> attachments = attachmentRepository.findAll();
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "获取所有附件成功");
			response.put("attachments", attachments);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("获取所有附件失败: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "获取所有附件失败: " + e.getMessage());
			return ResponseEntity.status(HttpStatusConstants.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}