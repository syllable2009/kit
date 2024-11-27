# 使用 Maven 官方镜像构建应用
FROM maven:3.8.6-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 将 pom.xml 和代码复制到容器中
COPY pom.xml .
COPY src ./src

# 使用 Maven 构建应用
RUN mvn clean package -DskipTests

# 使用 OpenJDK 官方镜像运行应用
FROM openjdk:17-jdk-slim

# 将构建好的 JAR 文件从构建阶段复制到运行阶段
COPY --from=build /app/target/*.jar app.jar

# 设置默认命令运行应用
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 暴露应用所需的端口
EXPOSE 8080
