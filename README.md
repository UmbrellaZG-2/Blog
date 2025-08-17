# UmbrellaZG 项目文档

## 项目介绍
这是一个前后端分离的项目，后端使用Spring Boot框架，前端使用React框架。本项目支持用户登录、文章管理、标签管理等功能。
（后端自己弄的，前端来自美团的nocode）
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