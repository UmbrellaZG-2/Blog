package com.website.backend.model;

import com.website.backend.constant.HttpStatusConstants;
import lombok.Data;

@Data
public class ApiResponse<T> {

	private int code;

	private String message;

	private T data;

	public ApiResponse() {
	}

	private ApiResponse(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(HttpStatusConstants.SUCCESS, "操作成功", data);
	}

	public static ApiResponse<Void> success() {
		return new ApiResponse<>(HttpStatusConstants.SUCCESS, "操作成功", null);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(HttpStatusConstants.SUCCESS, message, data);
	}

	public static <T> ApiResponse<T> fail(int code, String message) {
		return new ApiResponse<>(code, message, null);
	}

	public static <T> ApiResponse<T> fail(int code, String message, T data) {
		return new ApiResponse<>(code, message, data);
	}

	public static ApiResponse<Void> notFound() {
		return new ApiResponse<>(HttpStatusConstants.NOT_FOUND, "资源未找到", null);
	}

	public static ApiResponse<Void> unauthorized() {
		return new ApiResponse<>(HttpStatusConstants.UNAUTHORIZED, "未授权", null);
	}

	public static ApiResponse<Void> forbidden() {
		return new ApiResponse<>(HttpStatusConstants.FORBIDDEN, "禁止访问", null);
	}

	public static ApiResponse<Void> serverError() {
		return new ApiResponse<>(HttpStatusConstants.INTERNAL_SERVER_ERROR, "服务器内部错误", null);
	}

	public static ApiResponse<Void> fileFormatError() {
		return new ApiResponse<>(HttpStatusConstants.FILE_FORMAT_ERROR, "文件格式错误", null);
	}

	@Override
	public String toString() {
		return "ApiResponse{" + "code=" + code + ", message='" + message + '\'' + ", data=" + data + '}';
	}

}