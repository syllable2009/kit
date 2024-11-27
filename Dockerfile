# 使用 Maven 官方镜像构建应用
FROM maven:latest AS build

# 设置工作目录
WORKDIR /app

# 将 pom.xml 和代码复制到容器中
COPY pom.xml .
COPY kit-app/pom.xml kit-app/
COPY kit-app/src  kit-app/src

# 使用 Maven 构建应用
WORKDIR ./kit-app
RUN mvn clean package -DskipTests

# 使用 OpenJDK 官方镜像运行应用
FROM openjdk:17-jdk-slim

WORKDIR /app

# 将构建好的 JAR 文件从构建阶段复制到运行阶段
COPY --from=build /app/kit-app/target/*.jar app.jar

# 暴露应用所需的端口
EXPOSE 8080

# 设置默认命令运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]
