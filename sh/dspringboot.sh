#!/bin/bash

# Docker部署Spring Boot应用脚本
# 使用方法：./deploy.sh [profile] [port]

# 配置区域（根据实际项目修改）
PROJECT_PATH="/Users/jiaxiaopeng/github/kit/kit-hello"
PROJECT_NAME="kit-hello"     # 项目名称（需匹配pom.xml中的artifactId）
JAR_PATH="target/${PROJECT_NAME}-*.jar"    # JAR文件路径
DOCKER_IMAGE="springboot-app"              # Docker镜像名称
DOCKER_TAG="1.0"                           # 镜像标签
DEFAULT_PORT=8080                          # 默认映射端口

# ============= 参数处理 =============
PROFILE=${1:-"prod"}                # 第一参数：环境配置（默认pro）
PORT=${2:-$DEFAULT_PORT}           # 第二参数：映射端口（默认8080）

# ============= 1. 项目打包 =============
echo "🔨 开始打包项目 (使用${PROFILE}环境配置)..."
mvn clean package -DskipTests -P${PROFILE}

if [ $? -ne 0 ]; then
  echo "❌ 项目打包失败！请检查错误信息"
  exit 1
fi

# 查找生成的JAR文件
# 2>/dev/null：将错误输出重定向到空设备，避免路径不存在时报错干扰脚本执
jar_file=$(ls ${PROJECT_PATH}/${JAR_PATH} 2>/dev/null)
if [ -z "$jar_file" ]; then
  echo "❌ 未找到JAR文件: ${JAR_PATH}"
  exit 1
fi

echo "✅ 项目打包成功: $(ls -sh ${jar_file})"

# ============= 2. 准备Docker构建 =============
echo "🐳 准备Docker构建..."
cat > Dockerfile <<EOF
# 使用OpenJDK基础镜像
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 复制JAR文件到容器
COPY ${jar_file} app.jar

# 暴露端口
EXPOSE ${PORT}

# 启动应用（增加随机熵源加速启动）
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]
EOF

echo "📄 Dockerfile内容:"
echo "----------------------------------------"
cat Dockerfile
echo "----------------------------------------"

# ============= 3. 构建Docker镜像 =============
echo "🚢 构建Docker镜像: ${DOCKER_IMAGE}:${DOCKER_TAG}..."
docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .

if [ $? -ne 0 ]; then
  echo "❌ Docker镜像构建失败！"
  exit 1
fi
echo "✅ Docker镜像构建成功: $(docker images -q ${DOCKER_IMAGE}:${DOCKER_TAG})"

# ============= 4. 停止并移除旧容器 =============
container_id=$(docker ps -q --filter "name=^/${PROJECT_NAME}-container$")
if [ ! -z "$container_id" ]; then
  echo "🛑 停止并移除旧容器..."
  docker stop $container_id && docker rm $container_id
fi

# ============= 5. 运行新容器 =============
echo "🚀 启动新容器 (端口映射: ${PORT}:${PORT})..."
docker run -d \
  --name ${PROJECT_NAME}-container \
  -p ${PORT}:${PORT} \
  -v /var/log/${PROJECT_NAME}:/app/logs \
  ${DOCKER_IMAGE}:${DOCKER_TAG}

# ============= 6. 部署验证 =============
echo "⏳ 等待应用启动(5秒)..."
sleep 15

echo "📝 容器日志:"
docker logs --tail 50 ${PROJECT_NAME}-container

echo "🟢 部署完成!"
echo "👉 访问地址: http://localhost:${PORT}"
