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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attachments")
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

	@GetMapping("/download/{attachmentId}")
	public void downloadAttachment(@PathVariable Long attachmentId, HttpServletRequest request,
			HttpServletResponse response) {
		// 获取客户端IP地址
		String clientIp = getClientIpAddress(request);

		// 检查IP是否被阻止或超过下载限制
		if (rateLimitService.isIpBlocked(clientIp) || rateLimitService.recordDownloadRequest(clientIp)) {
			response.setStatus(HttpStatusConstants.FORBIDDEN);
			try {
				response.getWriter().write("下载频率过高，请24小时后再试");
			}
			catch (IOException ex) {
				// 忽略
			}
			return;
		}
		try {
			Attachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new IOException("Attachment not found"));
			byte[] fileContent = attachmentService.downloadAttachment(attachmentId);

			// 设置响应头
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + attachment.getFileName() + "\"");
			response.setContentLength(fileContent.length);

			// 写入响应体
			try (OutputStream os = response.getOutputStream()) {
				os.write(fileContent);
				os.flush();
			}
		}
		catch (IOException e) {
			response.setStatus(HttpStatusConstants.NOT_FOUND);
			try {
				response.getWriter().write("附件不存在: " + e.getMessage());
			}
			catch (IOException ex) {
				// 忽略
			}
		}
		catch (Exception e) {
			response.setStatus(HttpStatusConstants.INTERNAL_SERVER_ERROR);
			try {
				response.getWriter().write("下载附件失败: " + e.getMessage());
			}
			catch (IOException ex) {
				// 忽略
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
