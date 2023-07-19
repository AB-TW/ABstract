[![ci](https://github.com/AB-TW/ABstract/actions/workflows/gradle.yml/badge.svg)](https://github.com/pc-dong/ABstract/actions/gradle.yml)

# ABstract
An scaffold project for AB testing 


## TestContainer config
if use colima as test container, you need add the following system environment variables:
```shell
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/Users/peichao.dong/.colima/docker.sock
export DOCKER_HOST=unix:///{rootpath}/.colima/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```
add them to ~/.bash_profile and add add the follow config to the top of ~/.zshrc
```shell
if [ -f ~/.bash_profile ]; then
   source ~/.bash_profile
fi
```

## 技术栈

编程语言： Java 17

软件构件工具： Gradle 8.1.1

基础框架： Spring Boot 3.X

Web框架：Spring WebFlux、Spring RSocket

数据库： CouchBase + Spring Data reactive CouchBase

测试框架：JUnit5 +  Spring boot test + TestContainer

## 后端服务命令脚本

### 构建

```shell
./gradlew build --no-parallel
```

### 运行测试

```shell
./gradlew test --no-parallel
```

### 本地运行

```shell
# 启动数据库
docker-compose up -d


./gradlew bootrun
```

### Build Image

```shell
./gradlew build
./gradlew npmBuild
docker build -t a_bstract/abstract:latest .
```

## 模型

![domain](resource/domain.png)
