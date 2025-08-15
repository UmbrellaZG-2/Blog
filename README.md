# UmbrellaZG 项目文档

## 项目介绍
这是一个前后端分离的项目，后端使用Spring Boot框架，前端使用React框架。本项目支持用户登录、文章管理、标签管理等功能。

## 技术栈
- 后端：Spring Boot 3.4.5, Java 17, Spring Data JPA, Spring Security, MySQL, Redis
- 前端：React 18.2.0, Vite 5.4.11, Axios, Tailwind CSS

## 项目结构
```
WebZG/
├── BackEnd/        # 后端Spring Boot项目
├── FrontEnd/       # 前端React项目
└── SQL/            # 数据库脚本
```

## 本地开发启动步骤
请注意，所有application.yaml文件需要你自己准备，我的硬编码了，我就不提供了。

### 后端启动
1. 确保已安装JDK 17和Maven
2. 导入数据库脚本：`SQL/init.sql`
3. 修改数据库配置：`BackEnd/src/main/resources/application-prod.properties`
4. 进入后端目录：`cd BackEnd`
5. 启动后端服务：`mvn spring-boot:run`
   - 后端服务将运行在 http://localhost:8081/api

### 前端启动
1. 确保已安装Node.js和npm
2. 进入前端目录：`cd FrontEnd`
3. 安装依赖：`npm install`
4. 启动前端服务：`npm run dev`
   - 前端服务将运行在 http://localhost:8080

## 宝塔面板部署指南

### 1. 准备工作
- 一台运行 Linux 系统的服务器（推荐 CentOS 7.x 或 Ubuntu 18.04+）
- 服务器 root 权限
- 已备案的域名（可选，但推荐）
- 本地开发环境已完成项目代码编写和测试

### 2. 安装宝塔面板
1. 登录服务器终端（通过 SSH）
2. 执行以下命令安装宝塔面板（CentOS 系统）：
   ```bash
   yum install -y wget && wget -O install.sh http://download.bt.cn/install/install_6.0.sh && sh install.sh
   ```
   若为 Ubuntu/Debian 系统：
   ```bash
   wget -O install.sh http://download.bt.cn/install/install-ubuntu_6.0.sh && sudo bash install.sh
   ```
3. 安装完成后，记录面板登录地址、用户名和密码
4. 在浏览器中访问登录地址，输入用户名和密码登录宝塔面板

### 3. 配置服务器环境
1. 登录宝塔面板后，点击左侧「软件商店」
2. 安装以下必要软件：
   - Nginx（Web 服务器）
   - MySQL（数据库）
   - JDK 17（后端 Java 应用运行环境）
   - Maven（可选，用于构建后端项目）
   - Node.js（用于构建前端项目）
3. 配置 MySQL 数据库：
   - 点击左侧「数据库」
   - 创建新数据库，记录数据库名称、用户名和密码
   - 导入项目 SQL 目录下的 `init.sql` 文件
4. 开放服务器端口：
   - 点击左侧「安全」
   - 开放 80、443、8080、8081 等必要端口

### 4. 部署后端应用
1. **构建后端项目**：
   - 在本地开发环境中，进入 `BackEnd` 目录   
   - 执行 `mvn clean package` 命令构建项目
   - 构建完成后，在 `target` 目录下会生成 jar 包（如 `umbrellazg-0.0.1-SNAPSHOT.jar`）
2. **上传后端 jar 包**：
   - 在宝塔面板中，点击左侧「文件」
   - 进入 `/www/wwwroot` 目录，创建 `backend` 文件夹
   - 上传本地构建好的 jar 包到该目录
3. **创建后端运行服务**：
   - 点击左侧「软件商店」→「已安装」→「Java项目管理器」（若未安装，先在软件商店搜索安装）
   - 点击「添加项目」
   - 项目名称：`umbrellazg-backend`
   - 项目路径：`/www/wwwroot/backend`
   - JDK版本：选择已安装的 JDK 17
   - 启动jar：选择上传的 jar 包
   - 端口：`8081`（确保该端口未被占用）
   - 点击「提交」添加项目
   - 点击「启动」按钮启动后端服务
   - 或者使用命令行方式：
     ```bash
     # 进入后端目录
     cd /www/wwwroot/backend
     # 使用nohup命令后台运行
     nohup java -jar umbrellazg-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > backend.log 2>&1 &
     ```
4. **验证后端服务**：
   - 打开浏览器，访问 `http://服务器IP:8081/api/health`
   - 若返回 `{"status":"UP"}`，则后端服务启动成功

### 5. 部署前端应用
1. **构建前端项目**：
   - 在本地开发环境中，进入 `FrontEnd` 目录
   - 执行 `npm install` 安装依赖
   - 执行 `npm run build -- --mode production` 构建生产环境包
   - 构建完成后，会生成 `build` 目录
2. **上传前端静态文件**：
   - 在宝塔面板中，点击左侧「文件」
   - 进入 `/www/wwwroot` 目录，创建 `frontend` 文件夹
   - 上传本地 `build` 目录下的所有文件到该目录

### 6. 配置域名和反向代理
1. **添加网站**：
   - 点击左侧「网站」→「添加网站」
   - 域名：输入你的域名（如 `www.umbrellazg.com`）
   - 根目录：选择 `/www/wwwroot/frontend`
   - PHP版本：纯静态
   - 点击「提交」
2. **配置反向代理**：
   - 找到刚添加的网站，点击「设置」→「反向代理」→「添加反向代理」
   - 代理名称：`api-proxy`
   - 目标URL：`http://127.0.0.1:8081`
   - 发送域名：`$host`
   - 勾选「启用WebSocket代理」
   - 在「高级功能」中添加以下内容到「自定义Nginx配置」：
     ```nginx
     location /api {
         proxy_pass http://127.0.0.1:8081;
         proxy_set_header Host $host;
         proxy_set_header X-Real-IP $remote_addr;
         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         proxy_set_header X-Forwarded-Proto $scheme;
     }
     ```
   - 点击「保存」
3. **配置SSL证书**：
   - 点击左侧「网站」→ 找到目标网站 → 点击「SSL」
   - 选择「Let's Encrypt」→ 勾选要申请证书的域名 → 点击「申请」
   - 证书申请成功后，勾选「强制HTTPS」→ 点击「保存」
   - Let's Encrypt证书默认有效期为90天，宝塔面板会自动续期
4. **配置防盗链（可选）**：
   - 点击左侧「网站」→ 找到目标网站 → 点击「防盗链」
   - 开启防盗链 → 设置允许访问的域名 → 点击「保存」

### 7. 测试访问
1. 打开浏览器，访问你的域名（如 `http://www.umbrellazg.com`）
2. 若能正常显示前端页面，则前端部署成功
3. 尝试进行登录、查看数据等操作，验证前后端交互是否正常
4. 若所有功能正常，则部署成功

## 联调说明
1. 前端通过Vite代理将所有/api请求转发到后端服务
2. 后端API接口文档可通过访问 http://localhost:8081/api/swagger-ui.html 查看
3. 默认管理员账号：admin/admin123

## 功能列表
- 用户认证：登录、注册、注销
- 文章管理：创建、编辑、删除、查询文章
- 标签管理：创建、编辑、删除、查询标签
- 附件管理：上传、下载附件

## 注意事项
1. 确保后端服务启动前，MySQL和Redis服务已启动
2. 开发环境下，前端使用Vite代理进行跨域访问
3. 生产环境下，建议将前端静态资源部署到Nginx等Web服务器
4. 定期备份数据库和重要文件
5. 生产环境中，不要使用默认的管理员账号和密码，及时修改
6. 确保服务器防火墙已开放必要的端口
7. 定期更新服务器系统和软件，以保障安全性

## 常见问题
### 1. 后端服务启动失败怎么办？
- 检查MySQL和Redis是否已启动
- 检查数据库配置是否正确（用户名、密码、端口等）
- 查看后端日志文件，定位具体错误信息：`/www/wwwroot/backend/backend.log`
- 确保端口8081未被其他服务占用

### 2. 前端页面无法访问后端API怎么办？
- 检查后端服务是否正常运行
- 检查Nginx反向代理配置是否正确
- 查看浏览器控制台，检查是否有跨域错误或404错误
- 确保服务器防火墙已开放8081端口

### 3. 数据库连接失败怎么办？
- 检查MySQL服务是否正常运行
- 检查数据库用户名、密码和数据库名是否正确
- 确保MySQL允许远程连接（如果后端和数据库不在同一服务器）
- 检查数据库权限设置，确保用户有足够的权限

### 4. 如何更新项目？
- **后端更新**：
  1. 停止当前后端服务
  2. 上传新的jar包
  3. 启动后端服务
- **前端更新**：
  1. 在本地构建新的生产环境包
  2. 上传并替换`/www/wwwroot/frontend`目录下的文件
  3. 清除浏览器缓存或Nginx缓存

### 5. SSL证书申请失败怎么办？
- 确保域名已正确解析到服务器IP
- 确保端口443未被防火墙阻止
- 检查域名是否已被其他证书申请占用
- 尝试手动申请证书或使用其他证书提供商