# WebZG 后端接口文档

## 接口列表

### 1. ArticleController
处理文章相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/api/articles` | 获取文章列表 | 公开 |
| POST | `/api/articles/search` | 根据文章标题搜索文章 | 公开 |
| GET | `/api/articles/get/{id}` | 获取文章详情 | 公开 |
| GET | `/api/articles/category/get/{category}` | 获取分类下的文章 | 公开 |
| POST | `/api/articles/{articleId}/comments/put` | 添加评论 | 公开 |
| PUT | `/api/articles/comments/{commentId}/update` | 更新评论 | 管理员 |
| DELETE | `/api/articles/comments/{commentId}/delete` | 删除评论 | 管理员 |
| GET | `/api/articles/{articleId}/comments/get` | 获取文章的所有评论 | 公开 |
| GET | `/api/articles/categories/get` | 获取所有分类 | 公开 |
| POST | `/api/articles/{articleId}/tags/put` | 为文章添加标签 | 管理员 |
| DELETE | `/api/articles/{articleId}/tags/delete/{tagName}` | 删除文章的标签 | 管理员 |
| POST | `/api/articles/create` | 创建文章 | 管理员 |
| PUT | `/api/articles/update/{id}` | 更新文章 | 管理员 |
| DELETE | `/api/articles/delete/{id}` | 删除文章 | 管理员 |

### 2. ArticleFeatureController
处理文章功能相关的 API 请求（草稿和点赞）

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| POST | `/api/articles/draft` | 保存文章草稿 | 管理员 |
| GET | `/api/articles/drafts/{userId}` | 获取用户草稿 | 管理员 |
| POST | `/api/articles/draft/{id}/publish` | 发布文章草稿 | 管理员 |
| GET | `/api/articles/draft/check/{id}` | 检查文章是否为草稿 | 管理员 |
| POST | `/api/articles/{articleId}/like` | 点赞文章 | 公开 |
| POST | `/api/articles/{articleId}/unlike` | 取消点赞文章 | 公开 |
| GET | `/api/articles/{articleId}/likes` | 获取文章点赞数 | 公开 |
| GET | `/api/articles/{articleId}/like/check` | 检查用户是否已点赞文章 | 公开 |
| GET | `/api/articles/user/{userId}/likes` | 获取用户点赞的文章列表 | 公开 |

### 3. TagController
处理标签相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/api/tags/get` | 获取所有标签 | 公开 |
| GET | `/api/tags/list` | 获取所有标签列表 | 公开 |
| POST | `/api/tags/create` | 创建新标签 | 管理员 |
| DELETE | `/api/tags/delete/{id}` | 删除标签 | 管理员 |
| GET | `/api/tags/statistics` | 获取标签统计信息 | 公开 |
| GET | `/api/tags/article/{articleId}` | 获取文章的标签 | 公开 |
| GET | `/api/tags/recommend` | 推荐相关标签 | 公开 |
| GET | `/api/tags/popular` | 获取热门标签 | 公开 |

### 4. CommentController
处理评论相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/comments/article/{articleId}` | 获取文章的所有评论 | 公开 |
| POST | `/comments` | 创建评论 | 公开 |
| PUT | `/comments/{id}` | 更新评论 | 公开 |
| DELETE | `/comments/{id}` | 删除评论 | 管理员 |
| GET | `/comments/{id}` | 获取评论详情 | 公开 |

### 5. AttachmentController
处理附件相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/api/attachments/download/{attachmentId}` | 下载附件 | 公开 |
| POST | `/api/attachments/upload` | 上传附件 | 管理员 |
| DELETE | `/api/attachments/delete/{attachmentId}` | 删除附件 | 管理员 |
| GET | `/api/attachments/article/get/{articleId}` | 获取文章附件列表 | 公开 |
| GET | `/api/attachments/get` | 获取所有附件列表 | 管理员 |

### 6. ImageController
处理图片相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| POST | `/api/images/article/{articleId}/cover/update` | 上传文章封面图片 | 管理员 |
| DELETE | `/api/images/article/{articleId}/cover/delete` | 删除文章封面图片 | 管理员 |
| GET | `/api/images/article/{articleId}/cover/get` | 获取文章封面图片 | 公开 |
| GET | `/api/images/article/{articleId}/getAll` | 获取文章的所有图片 | 公开 |

### 7. AuthController
处理认证相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/api/auth/get` | 获取认证信息 | 公开 |
| POST | `/api/auth/login` | 用户登录 | 公开 |
| POST | `/api/auth/admin/login` | 管理员登录 | 公开 |
| POST | `/api/auth/admin/register` | 管理员注册 | 隐藏（前端不提供调用） |
| POST | `/api/auth/guest/login` | 游客登录(POST) | 公开 |
| GET | `/api/auth/guest/login` | 游客登录(GET) | 公开 |
| POST | `/api/auth/register/send-code` | 发送验证码 | 隐藏 |
| POST | `/api/auth/register/verify` | 验证验证码并注册 | 隐藏 |

### 8. HomeController
处理首页相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/api/home` | 首页接口 | 公开 |
| GET | `/api/home/aboutMe` | 关于我页面接口 | 公开 |