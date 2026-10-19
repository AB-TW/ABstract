![logo.png](resource/ABstractLogo.png)

[![ci](https://github.com/AB-TW/ABstract/actions/workflows/gradle.yml/badge.svg)](https://github.com/AB-TW/ABstract/actions/workflows/gradle.yml)

# ABstract
An scaffold project for AB testing 

## TestContainer config
if use colima as test container, you need add the following system environment variables:
```shell
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=$HOME/.colima/docker.sock
export DOCKER_HOST=unix://$HOME/.colima/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

add them to ~/.bash_profile and add add the follow config to the top of ~/.zshrc
```shell
if [ -f ~/.bash_profile ]; then
   source ~/.bash_profile
fi
```

The socket path depends on the Colima profile. A current default install often uses `$HOME/.colima/default/docker.sock` instead.

## 技术栈

编程语言： Java 21

软件构件工具： Gradle 8.14.1

基础框架： Spring Boot 4.0.6

Web框架：Spring WebFlux、Spring RSocket

数据库： Couchbase + Spring Data Reactive Couchbase（默认）/ PostgreSQL + R2DBC（备选，双持久化支持）

前端： Vue 3 + TypeScript + Element Plus + Vite

测试框架：JUnit 6 + Spring Boot Test + TestContainer

> ⚠️ 注意：测试目前因 JUnit 6 与 Mockito 不兼容而暂时禁用，详见 [TEST_COMPATIBILITY.md](TEST_COMPATIBILITY.md)

## 后端服务命令脚本

### 构建

```shell
./gradlew build -x test --no-parallel
```

### 运行测试

```shell
# ⚠️ 测试目前因 JUnit 6 + Mockito 兼容性问题暂时禁用
./gradlew test --no-parallel
```

### 本地运行

**Couchbase 版本 (默认):**

```shell
# 启动 Couchbase 数据库
docker-compose up -d

# 运行应用 (spring-boot-docker-compose 会自动检测并启动数据库)
./gradlew bootrun
```

**PostgreSQL 版本 (R2DBC):**

```shell
# 启动 PostgreSQL 数据库
docker-compose -f docker-compose-r2dbc.yml up -d

# 运行 R2DBC 应用
./gradlew :main-r2dbc:bootRun

# 或者指定环境变量运行
R2DBC_URL=r2dbc:postgresql://localhost:5432/abstract \
R2DBC_USERNAME=abstract \
R2DBC_PASSWORD=abstract \
./gradlew :main-r2dbc:bootRun
```

**停止数据库:**

```shell
# 停止 Couchbase
docker-compose down

# 停止 PostgreSQL
docker-compose -f docker-compose-r2dbc.yml down
```

### 本地演示口令

下面的账号只用于本机 Docker Compose 和示例配置，**不是生产口令**。部署到任何共享环境前请换成自己的密钥，不要把真实 `.env`、数据库文件或主机地址提交进仓库。

| 用途 | 用户 | 口令 | 位置 |
| --- | --- | --- | --- |
| Couchbase 管理员 | `Administrator` | `password` | `couchbase.Dockerfile` |
| Couchbase bucket | `bucket1` | `bucket1pass` | `couchbase.Dockerfile`、`main/src/main/resources/application.yml` |
| PostgreSQL | `abstract` | `abstract` | `docker-compose-r2dbc.yml` |

前端环境变量从 [`frontend/.env.example`](frontend/.env.example) 复制为 `frontend/.env.development`（或 `.env.staging` / `.env.production`）。这些文件已被忽略，示例里只有 localhost。

### Build Image

```shell
./gradlew build
./gradlew npmBuild
docker build -t a_bstract/abstract:latest .
```

## 模型

![domain](resource/domain.png)

## C端Rsocket接口RSC脚本示例

RSC是类似与curl命令的RSocket客户端命令行工具，方便对RSocket API进行调试，具体安装使用方法可以查看[GitHub - RSocket Client CLI (RSC)
](https://github.com/making/rsc).

### 1. Request-Stream模式

```shell
> rsc --route=customers.featureConfigs.{featureKey} --stream -m '{"customerId": "111","clientId":"610101","attributes":{}}' --mmt application/json   tcp://localhost:7001
> {"id":"027cc714-d47d-4fd7-b171-7013ffb02a21","featureKey":"{featureKey}","key":"1bd8e02d-7590-4662-86e6-b296a119c9b8","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":null,"priority":0,"updatedAt":"2023-08-22T17:01:00.497"}
> {"id":"60854696-4cdd-4d3e-bb3c-16447ae37f7d","featureKey":"{featureKey}","key":"7db5c4be-b513-4dcd-8152-7105ce49b08f","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":null,"priority":0,"updatedAt":"2023-08-22T17:00:58.056"}
> {"id":"fe521c69-6816-4557-ac37-04029925b2ad","featureKey":"{featureKey}","key":"b405968a-f117-459e-acea-63e0f1131489","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":"2023-08-29T16:00:00","priority":0,"updatedAt":"2023-08-22T17:00:47.004"}
 
```

### 2. Request-Channel模式

```shell
> rsc --route=customers.featureConfigs --channel -m '{"customerId": "111","clientId":"610101","attributes":{}}' --mmt application/json  --data=-  tcp://localhost:7001
> {featureKey}
> {"id":"027cc714-d47d-4fd7-b171-7013ffb02a21","featureKey":"{featureKey}","key":"1bd8e02d-7590-4662-86e6-b296a119c9b8","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":null,"priority":0,"updatedAt":"2023-08-22T17:01:00.497"}
> {"id":"60854696-4cdd-4d3e-bb3c-16447ae37f7d","featureKey":"{featureKey}","key":"7db5c4be-b513-4dcd-8152-7105ce49b08f","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":null,"priority":0,"updatedAt":"2023-08-22T17:00:58.056"}
> {"id":"fe521c69-6816-4557-ac37-04029925b2ad","featureKey":"{featureKey}","key":"b405968a-f117-459e-acea-63e0f1131489","data":{"key1":true,"key2":[""]},"saData":{},"expiredAt":"2023-08-29T16:00:00","priority":0,"updatedAt":"2023-08-22T17:00:47.004"}
 
```

## 项目介绍视频

https://github.com/user-attachments/assets/1470d2ed-d6f7-4205-b4da-22b37da5d450

44秒 | 全面了解 ABtract 平台的核心功能与技术架构（含真实界面演示）

---

## 系统功能

### 特性管理

**特性**指被测试软件的某个功能点，通常可以通过对应的配置项控制其表现行为（比如：是否展示/展示素材/控制流程等）。

特性管理模块提供了对特性的增删改查功能，以及基于**模版化**的配置能力。

### 实验管理

提供AB实验生命周期管理，配置主要包括实验分组比例、分组配置（基于特性模版）、实验分组结果统计能力。

### C端接口

实验和和固定配置通过统一接口获取，C端对是否有实验无感。可以顺畅的切换实验到实验后的固定配置。

## Licenses

仓库根目录的 [`LICENSE`](LICENSE) 是本项目的 MIT 许可。

[`frontend/LICENSE`](frontend/LICENSE) 保留管理端模板的上游 MIT 声明（Copyright (c) 2022 pany，<https://github.com/pany-ang>）。前端常量里的 `v3-admin-vite` 表明 UI 脚手架来自该模板。这份文件只说明模板归属，不替代根目录许可，也不表示模板作者拥有 ABstract 其余代码。
