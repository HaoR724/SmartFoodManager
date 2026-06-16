# 智膳管家

## 项目简介

智膳管家是一款基于 Android 的家庭食材管理与健康饮食推荐系统。本项目面向家庭日常饮食管理场景，主要用于解决食材容易遗忘、临期食材浪费、菜谱选择困难以及饮食营养不均衡等问题。

系统包括用户登录注册、首页概览、食材管理、食材过期提醒、菜谱推荐、购物清单、饮食记录和营养摄入分析等功能。项目采用 Android 原生客户端、Spring Boot 后端、MySQL 数据库和 Room/SQLite 本地数据库实现。

## 技术栈

* Android 原生开发
* Java
* XML 布局
* Room / SQLite
* RecyclerView
* Spring Boot
* Spring Data JPA
* MySQL
* HTTP / JSON

## 项目结构

```text
Chapter01/
├── app/                 # Android 客户端
├── server/              # Spring Boot 后端
├── gradle/              # Gradle Wrapper
├── build.gradle         # Android 项目构建配置
├── settings.gradle      # Android 项目配置
└── README.md            # 项目说明文档
```

## 系统架构

本项目采用“轻后端 + 本地业务数据库”的设计方式。

* 用户登录注册：Android 客户端调用 Spring Boot 后端接口，用户账号信息保存到 MySQL 数据库。
* 食材、菜谱、购物清单和饮食记录：主要保存到 Android 本地 Room/SQLite 数据库。
* 登录成功后，客户端会将后端返回的用户信息同步到本地数据库，方便本地业务数据继续按用户编号进行关联。

## 主要功能

### 1. 用户登录注册

用户可以在 Android 客户端完成注册和登录。客户端通过 HTTP 请求访问 Spring Boot 后端，后端连接 MySQL 数据库完成用户信息保存和账号密码校验。

后端接口：

```text
GET  /api/ping
POST /api/user/register
POST /api/user/login
```

### 2. 首页概览

首页展示食材数量、即将过期食材数量、已过期食材数量、推荐菜谱和饮食健康概览，方便用户快速了解当前食材库存和饮食状态。

### 3. 食材管理

用户可以添加、编辑、删除和查询食材。食材信息包括食材名称、分类、数量、单位、购买日期、过期日期和存放位置等。

### 4. 食材过期提醒

系统根据当前日期和食材过期日期自动判断食材状态，并将食材分为正常、即将过期和已过期三类，帮助用户及时处理临期食材。

### 5. 菜谱推荐

系统根据用户已有食材和菜谱所需食材计算匹配度，并优先推荐能够利用临期食材的菜谱。菜谱详情页面会展示已有食材、缺少食材、匹配度和推荐原因。

### 6. 购物清单

用户可以将菜谱中缺少的食材加入购物清单，也可以手动管理购物项。购物清单支持标记已购买和删除购物项。

### 7. 饮食健康分析

用户可以记录早餐、午餐、晚餐和加餐内容。系统根据食物营养数据估算热量、蛋白质、脂肪、碳水化合物和膳食纤维摄入情况，并给出下一餐饮食建议。

## 数据库说明

### MySQL 用户数据库

MySQL 用于保存用户账号信息。

默认数据库名称：

```text
smartfood
```

主要数据表：

```text
user_account
```

字段说明：

| 字段         | 说明         |
| ---------- | ---------- |
| id         | 用户编号，主键，自增 |
| username   | 用户名        |
| password   | 密码         |
| nickname   | 昵称         |
| created_at | 创建时间       |

后端数据库配置文件：

```text
server/src/main/resources/application.yml
```

数据库配置示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartfood?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:your_password}
```

说明：上传 GitHub 时不要填写真实数据库密码，建议使用环境变量或示例密码。

### Room / SQLite 本地数据库

Room/SQLite 用于保存 Android 客户端本地业务数据。

数据库文件名：

```text
smart_food.db
```

主要数据表：

```text
user
ingredient
recipe
recipe_ingredient
shopping_item
meal_record
food_nutrition
nutrition_target
```

本地数据库用于保存用户食材库存、菜谱数据、购物清单、饮食记录、营养数据和每日营养目标。

## 核心算法说明

### 食材过期提醒算法

系统根据当前日期和食材过期日期计算剩余天数：

```text
剩余天数 < 0：已过期
0 <= 剩余天数 <= 3：即将过期
剩余天数 > 3：正常
```

### 菜谱匹配推荐算法

系统根据用户已有食材和菜谱所需食材计算匹配度：

```text
匹配度 = 已有食材数量 / 菜谱所需食材总数量
临期食材利用率 = 菜谱中使用到的临期食材数量 / 用户临期食材总数量
最终推荐分数 = 匹配度 × 0.7 + 临期食材利用率 × 0.3
```

匹配度越高，说明用户当前越容易制作该菜谱；临期食材利用率越高，说明该菜谱越有利于减少食材浪费。

### 营养摄入估算算法

系统根据饮食记录中的食物名称和食用重量，结合营养数据表估算营养摄入：

```text
某营养素摄入量 = 食用重量 / 100 × 该食物每 100g 的营养含量
```

营养分析结果仅作为日常饮食参考，不作为医学诊断依据。

## 运行方法

### 1. 启动 MySQL

先启动 MySQL 数据库，并确认本地可以正常连接 MySQL。

默认数据库名称为：

```text
smartfood
```

如果数据库不存在，后端可根据配置自动创建。

### 2. 启动 Spring Boot 后端

进入后端目录：

```bash
cd server
```

使用 Maven 启动：

```bash
mvn spring-boot:run
```

后端默认运行端口：

```text
8081
```

启动成功后，可以访问：

```text
http://localhost:8081/api/ping
```

如果返回后端运行成功信息，说明 Spring Boot 服务启动正常。

### 3. 配置 Android 客户端接口地址

Android 客户端接口地址位于：

```text
app/src/main/java/com/example/smartfood/network/ApiClient.java
```

模拟器测试时可以使用：

```java
private static final String BASE_URL = "http://10.0.2.2:8081/api";
```

真机测试时，需要将地址改为电脑在同一 WiFi 下的 IPv4 地址，例如：

```java
private static final String BASE_URL = "http://电脑IP地址:8081/api";
```

注意：手机和电脑需要连接同一个 WiFi，且电脑防火墙需要允许 8081 端口访问。

### 4. 启动 Android 客户端

使用 Android Studio 打开项目根目录，等待 Gradle Sync 完成后，选择模拟器或 Android 真机运行 `app` 模块。

也可以使用命令行编译：

```bash
./gradlew assembleDebug
```

Windows 环境下可以使用：

```powershell
.\gradlew.bat assembleDebug
```

## 测试账号

可以在 App 注册页面创建新账号进行测试。

示例账号：

```text
用户名：admin
密码：123456
```

如果数据库中不存在该账号，可以手动注册新用户。

## 注意事项

上传 GitHub 前，建议不要上传以下文件或目录：

```text
.gradle/
.idea/
app/build/
server/target/
local.properties
*.apk
*.jks
```

不要上传真实数据库密码、服务器密码、API Key 或其他敏感信息。

如果真机无法连接后端，可以检查以下内容：

```text
1. Spring Boot 后端是否已经启动
2. 手机和电脑是否连接同一 WiFi
3. BASE_URL 是否改成电脑真实 IPv4 地址
4. Windows 防火墙是否允许 8081 端口访问
5. MySQL 是否已经启动
6. application.yml 中的数据库用户名和密码是否正确
```

## 后续改进方向

* 增加云端数据同步功能
* 增加多设备登录和数据备份功能
* 增加扫码录入食品功能
* 增加拍照识别食材功能
* 增加用户饮食偏好和过敏食材设置
* 优化菜谱推荐算法
* 扩充食物营养数据库
* 优化界面交互和动画效果

## 项目说明

本项目为课程设计作品，主要用于展示 Android 客户端开发、Room 本地数据库、Spring Boot 后端接口、MySQL 数据持久化以及移动端综合功能设计能力。
