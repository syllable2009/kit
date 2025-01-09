docker

Docker CLI -> Docker Engine(Docker Daemon) -> Containerd -> runc

Docker、Google、CoreOS 和其他供应商创建了开放容器计划 (OCI)，目前主要有两个标准文档：容器运行时标准 (runtime spec)和 容器镜像标准(image spec)。

其实containerd只是从Docker中分离出来的工业标准的容器运行时，更加开放、稳定的容器运行基础设施。在Linux和window上可以作为一个守护进程运行。
每个containerd只负责一台机器。Containerd 采用标准的 C/S 架构：服务端通过 GRPC 协议提供稳定的 API；客户端通过调用服务端的 API 进行高级的操作。
Docker将RunC捐赠给 OCI 作为OCI 容器运行时标准的参考实现。runc是容器核心技术实现，实现了容器启停、资源隔离等功能。

Dockerfile
由一条一条的指令组成，Docker程序将这些Dockerfile指令翻译真正的 Linux 命令。每条指令对应 Linux 下面的一条命令。

Dockerfile 中每一个指令都会建立一层。执行完后自动commit这一层构建新的镜像来复用。要避免臃肿多层的镜像。层数限制127。
时刻提醒自己这不是在写shell脚本，而是在定义每一层该如何构建。
FROM debian:stretch
RUN set -x; buildDeps='gcc libc6-dev make wget' \
&& apt-get update \
&& apt-get install -y $buildDeps \
&& wget -O redis.tar.gz "http://download.redis.io/releases/redis-5.0.3.tar.gz" \
&& mkdir -p /usr/src/redis \
&& tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1 \
&& make -C /usr/src/redis \
&& make -C /usr/src/redis install \
&& rm -rf /var/lib/apt/lists/* \
&& rm redis.tar.gz \
&& rm -r /usr/src/redis \
&& apt-get purge -y --auto-remove $buildDeps

$ docker build -t nginx:v3 -f path/to/your/Dockerfile .
.代表上下文路径，而不是指dockerfile所在的目录，这里和cs设计有关。
$ docker build -t hello-world https://github.com/docker-library/hello-world.git#master:amd64/hello-world
* -t, --tag <name[:tag]>：为生成的镜像指定名称和可选的标签。例如，myimage:latest。
* -f, --file <Dockerfile>：指定上下文路径路径。 Dockerfile 要在上下文目录的根目录下。

# FROM以一个镜像为基础，是必备的指令，并且必须是第一条指令。
FROM python:3.7-alpine
FROM scratch 空的镜像

# RUN用于在构建镜像时执行命令，并将结果保存到镜像的当前层中。
RUN <shell>格式：RUN echo '<h1>Hello, Docker!</h1>' > /usr/share/nginx/html/index.html
RUN <exec>格式：["可执行文件", "参数1", "参数2"]

# CMD在容器启动时指定默认运行的命令，但可以被覆盖。
在命令格式上推荐exec格式:CMD ["可执行文件", "参数1", "参数2"...],一定要用双引号数组的形式。
shell格式:CMD echo $HOME,实际执行中会被包装为sh -c的形式进行执行，CMD [ "sh", "-c", "echo $HOME" ]。
CMD ["nginx", "-g", "daemon off;"] 为什么需要daemon off;

前台执行，Docker 不是虚拟机，容器中的应用都应该以前台执行，而不是像虚拟机、物理机里面那样，用 systemd 去启动后台服务，容器内没有后台服务的概念。
在容器环境中，主进程通常需要在前台运行。如果主进程（在这个例子中是 Nginx）作为守护进程运行并转到后台，Docker 容器会认为主进程已经退出，从而导致容器退出。因此，使用daemon off;可以确保 Nginx 以前台方式运行，并且 Docker 可以正确管理容器的生命周期。
对于容器而言，其启动程序就是容器应用进程，容器就是为了主进程而存在的，主进程退出，容器就失去了存在的意义，从而退出，其它辅助进程不是它需要关心的东西。
CMD [ "sh", "-c", "echo $HOME" ]的主进程就变成了sh。命令结束后，sh 也就结束了，sh 作为主进程退出了，自然就会令容器退出。

# WORKDIR 指定工作目录。格式为 WORKDIR <工作目录路径>。
使用 WORKDIR 指令可以来指定工作目录（或者称为当前目录），以后各层的当前目录就被改为指定的目录，如该目录不存在，WORKDIR 会帮你建立目录。WORKDIR 指令使用的相对路径，那么所切换的路径与之前的 WORKDIR有关。
WORKDIR /a
WORKDIR b

# COPY 指令将从构建上下文目录中 <源路径> 的文件/目录复制到新的一层的镜像内的 <目标路径> 位置，ADD是COPY的高级版。
COPY package.json /usr/src/app/
COPY hom?.txt /mydir/
COPY hom* /mydir/
ADD ubuntu-xenial-core-cloudimg-amd64-root.tar.gz / 自动解压缩

# ENV设置环境变量，后面的其它指令，还是运行时的应用，都可以直接使用这里定义的环境变量
ENV <key> <value>
ENV <key1>=<value1> <key2>=<value2>...
ARG类似ENV，ARG 所设置的构建环境的环境变量，在将来容器运行时是不会存在这些环境变量的，docker history中可以看到。

# VOLUME 定义匿名卷
VOLUME /data
/data 目录就会在容器运行时自动挂载为匿名卷，任何向 /data 中写入的信息都不会记录进容器存储层，从而保证了容器存储层的无状态化。当然，运行容器时可以覆盖这个挂载设置。
docker run -d -v mydata:/data xxxx

# EXPOSE 暴露端口
EXPOSE 指令是声明容器运行时提供服务的端口，这只是一个声明，在容器运行时并不会因为这个声明应用就会开启这个端口的服务。
EXPOSE 8080

# 使用 ENTRYPOINT 定义容器的主命令，确保其不被覆盖。
ENTRYPOINT ["java", "-jar", "app.jar"]

Docker 使用 Google 公司推出的 Go 语言 进行开发实现，基于 Linux 内核的 cgroup，namespace，以及 OverlayFS 类的 Union FS 等技术，对进程进行封装隔离，属于 操作系统层面的虚拟化技术。

docker pull [选项] [仓库地址[:端口号]/][仓库名][镜像名:标签]
docker pull docker.io/library/ubuntu:18.04 = docker pull ubuntu:18.04

docker run -it --rm ubuntu:18.04 bash = docker run -t -i ubuntu:18.04 /bin/bash
-i：交互式操作，一个是 -t 终端。--rm：这个参数是说容器退出后随之将其删除。

docker export/import 导入导出镜像

docker和docker compose 和Dockerfile

docker run
-d 运行容器在后台模式（detached mode），容器会在后台运行并打印容器ID。
--name myWebApp 为容器指定一个名称 myWebApp，方便后续管理和引用。
--restart=always 设置容器的重启策略为 always，这意味着当容器退出时，Docker 会自动重启它，除非它是显式停止的。
-p 8080:80 端口映射，这里将容器内部的 80 端口映射到宿主机的 8080 端口，这意味着外部请求到宿主机的 8080 端口会被转发到容器的 80 端口。
-e "ENV=production" 设置环境变量，这里设置了一个名为 ENV 的环境变量，其值为 production，应用程序可以根据该变量调整运行模式。
--memory=500m 限制容器使用的最大内存为 500MB。
--cpu-shares=1024 设置容器 CPU 权重，在 CPU 资源竞争时，Docker 会根据权重分配 CPU 时间片。默认值是 1024。
-v /my/local/path:/var/www/html 卷挂载，将宿主机的 /my/local/path 目录挂载到容器的 /var/www/html，这通常用于持久化数据存储或配置文件的读取。
--network=my-network 将容器连接到一个已经存在的网络 my-network，这对于容器间的通信很有用。
myWebImage 最后这部分是你想要运行的 Docker 镜像名称，这里示例为 myWebImage。

# Docker 多阶段构建
FROM node:14 AS build-stage 这是一个多阶段构建的第一阶段，它使用 Node.js 14 版本的官方镜像作为基础镜像，并命名此阶段为 build-stage。
WORKDIR /app 设置工作目录为 /app，所有后续的 RUN, CMD, ENTRYPOINT, COPY, ADD 指令都将在这个目录下执行。 COPY package*.json ./ 将包含项目依赖信息的 package.json 和 package-lock.json 文件复制到容器的工作目录中。
RUN npm install 安装定义在 package.json 中的依赖。
COPY . . 将当前目录下的所有文件（除了 .dockerignore 中指定的文件和目录）复制到容器的工作目录中。
RUN npm run build 执行构建命令，生成用于生产的代码。
FROM nginx:alpine 多阶段构建的第二阶段，以轻量级的 nginx 镜像为基础。
COPY --from=build-stage /app/build /usr/share/nginx/html 从构建阶段 build-stage 中复制构建好的文件到 nginx 镜像的相应目录下。
EXPOSE 80 指定容器在运行时监听的端口号，这里是 nginx 的默认端口。 CMD ["nginx", "-g", "daemon off;"] 容器启动时执行的命令，启动 nginx 并以前台模式运行。


docker-compose.yml
Compose 它允许用户通过一个单独的 docker-compose.yml 模板文件（YAML 格式）来定义一组相关联的应用容器为一个项目（project）。

项目包含很多服务
version: '3.8'

services:
web:
image: nginx:latest
ports:
- "80:80"
volumes:
- ./html:/usr/share/nginx/html
depends_on:
- db

db:
image: mysql:5.7
environment:
MYSQL_ROOT_PASSWORD: example
MYSQL_DATABASE: exampledb
MYSQL_USER: exampleuser
MYSQL_PASSWORD: examplepass
volumes:
- db_data:/var/lib/mysql

volumes:
db_data

docker-compose up

version指定使用的 docker-compose 文件版本，这里使用的是 3.8，这通常与 Docker Engine 的版本相关联。 services 定义应用中的各个服务。 web: image 指定服务使用的镜像，这里假设有一个标记为 latest 的 my-web-app 镜像。 build 定义如何构建镜像，可以指定上下文（通常是 Dockerfile 所在的目录）和 Dockerfile 的名称。 ports 端口映射。将容器的 5000 端口映射到宿主机的 5000 端口。 environment 设置环境变量。 depends_on 表示 web 服务依赖于 db 服务。 networks 指定该服务连接的网络。 db: image 使用的数据库镜像，这里使用的是 postgres:13。 volumes 卷挂载，用于持久化数据库数据。 environment 设置数据库的环境变量，如数据库名、用户和密码。 networks 指定该服务连接的网络。 networks 定义网络，app-network 使用了默认的 bridge 驱动，它允许容器间通信。 volumes 定义卷，db-data 使用了本地驱动，用于持久化数据库数据。