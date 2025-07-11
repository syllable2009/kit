核心概念
服务（Service）：一个应用程序的组件，比如web服务器、数据库等
项目（Project）：由一组关联的服务组成的完整应用，项目=服务1...服务N
容器（Container）：服务的运行实例

Docker Compose 是一个用于定义和运行多容器Docker应用程序的工具。负责实现对 Docker 容器集群的快速编排。
docker compose up -d
docker compose ps
docker compose config 来校验配置
特性	传统Docker命令	Docker Compose
启动复杂度	需要多个长命令	一个命令
配置管理	分散在各个命令中	集中在YAML文件
服务依赖	手动管理启动顺序	自动处理依赖
网络配置	手动创建和连接	自动创建
数据卷管理	分别创建	统一管理
环境一致性	容易出错	配置文件保证一致性

Docker Compose允许用户通过一个单独的 docker-compose.yml 模板文件（YAML 格式）来定义一组相关联的应用容器为一个项目（project）。

YAML（YAML Ain't Markup Language）是一种人类可读的数据序列化标准。
1. 缩进表示层级关系
2. 冒号后面必须有空格
3. 列表使用短横线
4. 字符串可以有引号或无引号
5. 布尔值和数字
6. YAML严格禁止使用Tab键缩进，必须使用空格（推荐2或4个空格）



# docker-compose.yml
version: '3.8' # 版本号，与docker版本有关，目前都为3.8
services: # 服务定义
    db: #服务1
        image: mysql:8.0
        environment:
            MYSQL_ROOT_PASSWORD: 123456
            MYSQL_DATABASE: myapp
        volumes:
        - mysql_data:/var/lib/mysql
    redis: #服务2
        image: redis:7-alpine
    app: #服务3
        image: my-spring-app:latest
        container_name: abc
        restart: unless-stopped
        ports: # 暴露端口信息。HOST:CONTAINER
            - "3000" # 宿主将会随机选择端口     
            - "8080:8080"
        volumes: #-v数据卷所挂载路径设置，HOST:CONTAINER:ro
            - mysql_data:/var/lib/mysql
            - ~/configs:/etc/configs/:ro
            - /var/lib/mysql # 此配置会将该路径直接映射到容器内的相同路径/var/lib/mysql
        environment: # 参数-e，使用数组或字典两种格式
            - RACK_ENV=development
            - SESSION_SECRET: # 只给定名称的变量会自动获取运行 Compose 主机上对应变量的值，可以用来防止泄露不必要的数据。
        environment:    
            DATABASE_URL: mysql://root:123456@db:3306/myapp
            REDIS_URL: redis://redis:6379
        expose: # 暴露端口，但不映射到宿主机，只被连接的服务访问
            - "3000"
            - "8000"
        depends_on: #依赖关系
            - db
            - redis

构建配置
