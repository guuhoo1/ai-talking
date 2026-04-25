# AI Talking

AI Talking是一个基于Vue 3和Spring Boot的智能聊天应用，使用Ollama提供AI模型支持。

## 项目结构

```
ai-talking/
├── backend/         # 后端Spring Boot项目
├── frontend/        # 前端Vue 3项目
├── docker-compose.yml  # Docker Compose配置
├── init_db.sql      # 数据库初始化脚本
└── insert_admin.sql # 管理员账号插入脚本
```

## 技术栈

### 前端
- Vue 3 + TypeScript
- Pinia (状态管理)
- Ant Design Vue (UI组件库)
- Axios (HTTP客户端)

### 后端
- Spring Boot 3
- MyBatis (ORM框架)
- Sa-Token (权限认证)
- Ollama (AI模型服务)

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- Docker (可选)
- Ollama服务

### 本地运行

#### 1. 启动Ollama服务

请确保本地已经安装并启动了Ollama服务，并且下载了所需的模型（如qwen2.5:3b、llama3、mistral）。

#### 2. 启动后端服务

```bash
# 进入后端目录
cd backend

# 启动服务
./start.bat
```

#### 3. 启动前端服务

```bash
# 进入前端目录
cd frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

### Docker运行

```bash
# 在项目根目录执行
docker-compose up -d
```

## 功能特性

- 多会话管理
- 支持多种AI模型
- 流式响应（实时显示AI回复）
- 会话历史记录
- 用户认证系统

## API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/user/info` - 获取用户信息
- `POST /api/auth/logout` - 用户登出

### 会话相关
- `GET /api/chat/session/list` - 获取会话列表
- `POST /api/chat/session/create` - 创建新会话
- `DELETE /api/chat/session/{id}` - 删除会话
- `GET /api/chat/message/{sessionId}` - 获取会话消息
- `GET /api/chat/send` - 发送消息（流式响应）

## 项目配置

### 后端配置
- 数据库配置：`backend/src/main/resources/application.yml`
- Ollama服务配置：`backend/src/main/java/com/aitalking/utils/OllamaClient.java`

### 前端配置
- API基础URL：`frontend/src/utils/request.ts`

## 注意事项

1. 确保Ollama服务已经启动并运行
2. 确保数据库连接正确配置
3. 首次运行时会自动创建数据库表结构
4. 默认管理员账号：admin / 123456

## 许可证

MIT License
