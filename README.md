<p align="center">
  <img src="https://img.shields.io/badge/Java-8-orange?logo=openjdk" alt="Java 8">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen?logo=springboot" alt="Spring Boot 2.7.12">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.7-red" alt="MyBatis-Plus 3.5.7">
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql" alt="MySQL 8.0+">
  <img src="https://img.shields.io/badge/Redis-6.2-red?logo=redis" alt="Redis 6.2">
  <img src="https://img.shields.io/badge/RabbitMQ-gray?logo=rabbitmq" alt="RabbitMQ">
  <img src="https://img.shields.io/badge/Apache%20Dubbo-3.0.9-blue?logo=apachedubbo" alt="Apache Dubbo 3.0.9">
  <img src="https://img.shields.io/badge/Nacos-2.1.0-blue?logo=alibabacloud" alt="Nacos 2.1.0">
  <img src="https://img.shields.io/badge/Alipay-Sandbox-1677FF?logo=alipay" alt="Alipay Sandbox">
  <img src="https://img.shields.io/badge/WeChat-QR%20Login-07C160?logo=wechat" alt="WeChat QR Login">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License MIT">
</p>

<h1 align="center">🛍️ Small Pay Mall</h1>

<p align="center">
  <b>基于 Spring Boot 的轻量级聚合支付商城系统</b><br>
  集成支付宝支付 · 微信扫码登录 · 拼团营销 · 消息驱动的现代化电商后端
</p>

---

## 📋 项目简介

**Small Pay Mall** 是一个面向二次元周边的轻量级电商支付系统。用户可以通过 **微信扫码登录** 后浏览商品，选择 **支付宝** 完成支付，并支持 **拼团营销** 等促销活动。

项目采用 **Spring Boot 2.7** 多模块分层架构，整合了 **Redis** 缓存/发布订阅、**RabbitMQ** 消息队列、**MyBatis-Plus** ORM、**Dubbo + Nacos** 微服务调用等主流中间件，适合作为电商支付领域的实战项目或学习参考。

### 🎯 核心业务流程

```
用户 → 微信扫码登录 → 浏览商品 → 创建订单 → 支付宝支付 → 支付回调 → 库存扣减 → 拼团结算
                                                                       ↓
                                                                 微信模板消息通知
```

---

## ✨ 功能特性

### 💰 支付系统
- **支付宝电脑网站支付**（沙箱环境）
- 支付宝异步通知处理 + RSA2 签名验证
- 订单退款能力（含库存回滚）
- 定时关单（每 30 分钟关闭超时未支付订单）
- 订单全生命周期管理：`CREATE → PAY_WAIT → PAY_SUCCESS → DEAL_DONE / CLOSE / REFUND`

### 🔐 微信登录
- 微信公众号二维码扫码登录
- 临时二维码生成（30 天有效期）
- Access Token Redis 缓存管理
- 扫码事件推送处理 + 登录模板消息通知

### 👥 拼团营销
- 支持普通购买和拼团两种营销模式
- Dubbo RPC 对接外部拼团平台锁定优惠
- 支付成功后同步/异步两种结算路径
  - **同步**: 支付宝支付成功回调时立即结算
  - **异步**: RabbitMQ 监听拼团成团事件批量结算
- HTTP 回调支持（`/pay/group_buying_notify`）

### 📦 商品与库存
- 商品列表展示（支持 Redis 缓存，1 天过期）
- 特殊/推荐商品标记
- 支付成功自动扣减库存
- 退款成功自动回滚库存

### 📨 消息通知
- 微信模板消息（登录成功、支付成功、退款通知）
- Redis 发布/订阅处理库存变更
- RabbitMQ 主题交换机接收成团事件

---

## 🏗️ 技术栈

| 类别 | 技术 | 用途 |
|------|------|------|
| **框架** | Spring Boot 2.7.12 | 应用框架 |
| **语言** | Java 8 | 开发语言 |
| **ORM** | MyBatis-Plus 3.5.7 | 数据库 ORM |
| **数据库** | MySQL 8.0+ | 数据存储 |
| **缓存** | Redis 6.2 | 缓存 / 发布订阅 |
| **消息队列** | RabbitMQ | 异步事件驱动 |
| **RPC** | Apache Dubbo 3.0.9 | 服务间调用 |
| **注册中心** | Nacos 2.1.0 | 服务发现 |
| **支付** | Alipay SDK 4.38.157 | 支付宝支付/退款 |
| **微信** | Retrofit2 + 微信官方 API | 二维码登录 / 模板消息 |
| **API 文档** | Swagger (Spring Boot) | (可选集成) |

---

## 🧱 项目结构

```
small-pay-mall/
├── small-pay-mall-common/          # 公共模块
│   ├── annotation/                  # 自定义注解 (RedisTopic)
│   ├── constants/                   # 常量 & 枚举 (ResponseCode, OrderStatus)
│   ├── context/                     # 线程上下文 (ThreadContext)
│   ├── exception/                   # 统一异常 (AppException)
│   ├── response/                    # 统一响应封装 (Response)
│   └── weixin/                      # 微信工具 (消息解析、签名校验)
│
├── small-pay-mall-domain/          # 领域模型
│   ├── dto/                         # 数据传输对象
│   ├── entity/                      # 领域实体
│   ├── enums/                       # 业务枚举
│   ├── po/                          # 数据库持久化对象
│   └── vo/                          # 视图对象
│
├── small-pay-mall-mapper/          # 数据访问层
│   ├── ItemMapper                   # 商品 Mapper
│   └── PayOrderMapper               # 订单 Mapper
│
├── small-pay-mall-service/         # 业务逻辑层
│   ├── config/                      # 支付宝配置
│   ├── impl/                        # 服务实现
│   │   ├── ItemServiceImpl          # 商品服务
│   │   ├── PayOrderServiceImpl       # 支付订单服务
│   │   └── WXLoginServiceImpl       # 微信登录服务
│   ├── port/                        # RPC 端口接口
│   └── service/                     # 服务接口
│
└── small-pay-mall-web/             # Web 接入层
    ├── config/                      # 全局配置 (Redis/Retrofit2/Web)
    ├── controller/                  # REST 控制器
    ├── interceptor/                 # 登录拦截器
    ├── listener/                    # 消息监听器 (Redis + RabbitMQ)
    ├── port/                        # RPC 端口实现 (Dubbo)
    └── task/                        # 定时任务
```

---

## 🚀 快速开始

### 前置要求

| 环境 | 版本要求 | 备注 |
|------|---------|------|
| JDK | 8+ | 推荐 JDK 8 |
| Maven | 3.6+ | 构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.2+ | 缓存/消息 |
| RabbitMQ | 任意版本 | 异步消息 |
| Nacos | 2.1.0 | 注册中心 |

### 1️⃣ 克隆项目

```bash
git clone https://github.com/your-username/small-pay-mall.git
cd small-pay-mall
```

### 2️⃣ 初始化数据库

执行 `docs/sql/` 目录下的 SQL 脚本创建 `pay-mall` 数据库和表结构：

```bash
mysql -u root -p < docs/sql/20250324-lock-pay-order-pay-mall.sql
```

### 3️⃣ 启动基础设施

```bash
docker-compose -f docs/docker-compose-environment-aliyun.yml up -d
```

### 4️⃣ 修改配置

编辑 `small-pay-mall-web/src/main/resources/application-dev.yaml`，根据你的环境修改：

- MySQL 数据库连接
- Redis 连接
- RabbitMQ 连接
- Nacos 注册中心地址
- 微信配置（AppID、AppSecret 等）
- 支付宝沙箱配置

### 5️⃣ 启动应用

```bash
mvn clean package -DskipTests
mvn spring-boot:run -pl small-pay-mall-web
```

访问 http://localhost:8080/hi 确认服务正常运行。

---

## 📡 API 接口

### 健康检查

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/hi` | 服务健康检查 |
| GET | `/wx/portal/hi` | 微信门户健康检查 |

### 商品

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/item/show` | 查询所有商品 |
| GET | `/item/show/special` | 查询推荐商品（Redis 缓存） |

### 支付订单

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/pay/order/create` | 创建订单（支持拼团） |
| POST | `/pay/order` | 获取订单支付链接 |
| POST | `/pay/notify` | 支付宝异步回调 |
| GET | `/pay/order/search` | 查询用户订单列表 |
| POST | `/pay/order/refund` | 发起退款 |
| GET | `/pay/order/remind/{orderId}` | 订单提醒/发货 |

### 微信登录

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/wx/login/qrcode/create` | 创建登录二维码 |
| POST | `/wx/login/check` | 检查扫码登录状态 |

### 微信回调

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/wx/portal/receive` | 微信服务器验证 |
| POST | `/wx/portal/receive` | 接收微信推送事件 |

### 拼团回调

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/pay/group_buying_notify` | 拼团完成 HTTP 回调 |

---

## 🗄️ 数据库设计

### `item` — 商品表

| 字段 | 类型 | 说明 |
|------|------|------|
| item_id | varchar(32) | 商品 ID（唯一） |
| item_name | varchar(16) | 商品名称 |
| item_image | varchar(64) | 商品图片 URL |
| item_desc | varchar(64) | 商品描述 |
| item_quantity | int | 库存（默认 999） |
| amount | decimal(8,2) | 价格 |
| item_status | int | 状态（0=普通，1=推荐） |

### `pay_order` — 订单表

| 字段 | 类型 | 说明 |
|------|------|------|
| order_id | varchar(32) | 订单 ID（唯一，12 位数字） |
| user_id | varchar(32) | 用户 ID |
| item_id | varchar(32) | 商品 ID |
| total_amount | decimal(8,2) | 订单总金额 |
| discount_amount | decimal(8,2) | 优惠金额 |
| pay_amount | decimal(8,2) | 实际支付金额 |
| status | varchar(32) | 订单状态 |
| market_type | tinyint(1) | 营销类型（0=无，1=拼团） |

### 订单状态流转

```
CREATE ──→ PAY_WAIT ──→ PAY_SUCCESS ──→ DEAL_DONE
  │            │              │
  │            │              └──→ REFUND
  │            └──→ CLOSE
  └──→ CLOSE (定时关单)
```

---

## 🔧 环境配置

项目提供两套运行配置：

- **application-dev.yaml** — 开发环境（默认）
- **application-prod.yaml** — 生产环境

通过 `spring.profiles.active` 切换。关键配置包括：

| 配置项 | 说明 |
|--------|------|
| `wx.app-id` / `wx.app-secret` | 微信公众号凭据 |
| `alipay.app-id` | 支付宝应用 ID |
| `alipay.merchant-private-key` | 商户私钥 |
| `alipay.alipay-public-key` | 支付宝公钥 |
| `dubbo.registry.address` | Nacos 注册中心地址 |

---

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -pl small-pay-mall-web -Dtest=PayTest
```

---

## 📄 License

[MIT License](LICENSE)

Copyright (c) 2024-2025

---

<p align="center">
  <b>Small Pay Mall</b> — 一个完整、可运行的电商支付系统<br>
  如果这个项目对你有帮助，欢迎 ⭐ Star 支持！
</p>
