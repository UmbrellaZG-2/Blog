package com.website.backend.common.constant;

public class HttpStatusConstants {

	// 成功状态码
	public static final int SUCCESS = 200; // 成功

	public static final int CREATED = 201; // 创建成功

	public static final int NO_CONTENT = 204; // 无内容

	// 客户端错误状态码
	public static final int BAD_REQUEST = 400; // 请求错误

	public static final int UNAUTHORIZED = 401; // 未授权

	public static final int FORBIDDEN = 403; // 禁止访问

	public static final int NOT_FOUND = 404; // 资源未找到

	public static final int METHOD_NOT_ALLOWED = 405; // 方法不允许

	public static final int REQUEST_TIMEOUT = 408; // 请求超时

	public static final int CONFLICT = 409; // 冲突

	public static final int UNSUPPORTED_MEDIA_TYPE = 415; // 不支持的媒体类型

	// 服务器错误状态码
	public static final int INTERNAL_SERVER_ERROR = 500; // 服务器内部错误

	// 自定义错误状态码
	public static final int FILE_FORMAT_ERROR = 1001; // 文件格式错误

	public static final int FILE_SIZE_EXCEEDED = 1002; // 文件大小超出限制

	public static final int FILE_UPLOAD_ERROR = 1003; // 文件上传错误

	public static final int VERIFICATION_CODE_ERROR = 1004; // 验证码错误

	public static final int USER_ALREADY_EXISTS = 1005; // 用户已存在

	public static final int USER_NOT_FOUND = 1006; // 用户不存在

	public static final int INVALID_PASSWORD = 1007; // 密码错误

	public static final int TOKEN_EXPIRED = 1008; // 令牌过期
}