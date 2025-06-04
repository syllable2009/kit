#!/bin/bash
# 参数定义
IMAGE="nginx:alpine"
CONTAINER_NAME="ng"
INSTANCE_COUNT=1
DATA_DIR="/Users/jiaxiaopeng/docker"

# 创建数据目录，Bash的花括号扩展语法
mkdir -p $DATA_DIR/{conf,logs,html}
# chmod -R‌，用于递归修改目录及其子目录和文件的权限。
chmod -R 777 $DATA_DIR

# 多实例部署
for i in $(seq 1 $INSTANCE_COUNT); do
  docker run -d \
    --name ${CONTAINER_NAME}-${i} \
    -p 80${i}:80 \
    -v $DATA_DIR/conf:/etc/nginx \
    -v $DATA_DIR/logs:/var/log/nginx \
    -v $DATA_DIR/html:/usr/share/nginx/html \
    $IMAGE
done