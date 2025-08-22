
| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/articles` | 获取文章列表 | 公开 |
| GET | `/articles/search` | 根据文章标题搜索文章 | 公开 |
| POST | `/articles/create` | 创建文章 | 管理员 |
| PUT | `/articles/update/{id}` | 更新文章 | 管理员 |
| DELETE | `/articles/delete/{id}` | 删除文章 | 管理员 |
| GET | `/articles/get/{id}` | 获取文章详情 | 公开 |
| GET | `/articles/category/get/{category}` | 获取分类下的文章 | 公开 |
| POST | `/articles/{articleId}/comments/put` | 添加评论 | 公开 |
| GET | `/articles/{articleId}/comments/get` | 获取文章的所有评论 | 公开 |
| GET | `/articles/categories/get` | 获取所有分类 | 公开 |
| POST | `/articles/{articleId}/tags/put` | 为文章添加标签 | 管理员 |
| DELETE | `/articles/{articleId}/tags/delete/{tagName}` | 删除文章的标签 | 管理员 |

### 2. AttachmentController
处理附件相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/attachments/download/{attachmentId}` | 下载附件 | 公开 |
| POST | `/attachments/upload` | 上传附件 | 管理员 |
| DELETE | `/attachments/delete/{attachmentId}` | 删除附件 | 管理员 |
| GET | `/attachments/article/get/{articleId}` | 获取文章附件列表 | 公开 |
| GET | `/attachments/get` | 获取所有附件列表 | 管理员 |

### 3. AuthController
处理认证相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/auth/get` | 获取认证信息 | 公开 |
| POST | `/auth/login` | 用户登录 | 公开 |
| POST | `/auth/admin/login` | 管理员登录 | 公开 |
| POST | `/auth/admin/register` | 管理员注册 | 隐藏（前端不提供调用） |
| POST | `/api/auth/guest/login` | 游客登录(POST) | 公开 |
| GET | `/api/auth/guest/login` | 游客登录(GET) | 公开 |
| POST | `/api/auth/register/send-code` | 发送验证码 | 隐藏 |
| POST | `/api/auth/register/verify` | 验证验证码并注册 | 隐藏 |

### 4. HomeController
处理首页相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| GET | `/home` | 首页接口 | 公开 |
| GET | `/home/redirect/aboutMe` | 跳转到关于我页面 | 公开 |
| GET | `/home/aboutMe` | 关于我接口 | 公开 |

### 5. ImageController
处理图片相关的 API 请求

| 方法 | 路径 | 功能描述 | 访问权限 |
|------|------|----------|----------|
| POST | `/images/article/{articleId}/cover/update` | 上传文章封面图片 | 管理员 |
| DELETE | `/images/article/{articleId}/cover/delete` | 删除文章封面图片 | 管理员 |
| GET | `/images/article/{articleId}/cover/get` | 获取文章封面图片 | 公开 |
| GET | `/images/article/{articleId}/getAll` | 获取文章的所有图片 | 公开 |
