#!/bin/bash
PROJECT_PATH="/Users/jiaxiaopeng/github/kit/kit-font"

# cd到工作目录，否则为当前sh的目录
cd ${PROJECT_PATH}
echo "当前目录: $(pwd)"

# 检测target目录下的JAR包
JAR_FILE=$(ls target/*.jar 2>/dev/null | head -n 1)
FILE_NAME=""

if [ -f "$JAR_FILE" ]; then
  # 表示从变量JAR_NAME的‌开头‌删除匹配*/的最短字符串,##：表示从开头删除最长匹配,*/：匹配任意字符直到最后一个/
    echo "${JAR_FILE}"
    FILE_NAME=${JAR_FILE##*/}
    echo "检测到已存在的JAR包: ${FILE_NAME}"
else
    echo "未检测到JAR包，开始编译项目..."
    mvn clean package -DskipTests

    if [ $? -eq 0 ]; then
        NEW_JAR=$(ls target/*.jar | head -n 1)
        FILE_NAME=${NEW_JAR##*/}
        echo "编译成功，生成的JAR包: ${FILE_NAME}"
    else
        echo "编译失败，请检查错误" >&2
        exit 1
    fi
fi

# 2. 准备Dockerfile
mkdir -p dockerfile
rm -v dockerfile/Dockerfile
cat > dockerfile/Dockerfile <<EOF
FROM openjdk:11-jre-slim
VOLUME /tmp
WORKDIR /app
COPY target/$FILE_NAME app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar","--spring.profiles.active=prod","-c"]
EOF

# 3. 构建Docker镜像
#echo "正在构建Docker镜像..."
docker build -t springboot-app -f dockerfile/Dockerfile .

# 4. 启动容器服务
# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "镜像构建成功，正在启动服务..."
    docker run -d -p 8087:8080 --name my-springboot-app springboot-app:latest
    echo "容器已启动，可通过 http://localhost:8087 访问"
else
    echo "镜像构建失败，请检查Dockerfile" >&2
    exit 1
fi





