## 收集的docker项目

# 一款实用的个人IT工具箱——it-tools
https://hub.docker.com/r/corentinth/it-tools
开源项目：https://github.com/CorentinTh/it-tools
docker run -d --name it-tools --restart unless-stopped -p 8800:80 corentinth/it-tools:latest

# 阅读3网页版
https://hub.docker.com/r/hectorqin/reader
开源项目：https://github.com/celetor/web-yuedu3
docker run -d --restart=always \
--name=reader -e "SPRING_PROFILES_ACTIVE=prod" \
-v /docker/reader/logs:/logs -v /docker/reader/storage:/storage \
-p 9800:8080 hectorqin/reader:3.2.11

# 1panel
https://1panel.cn/docs/installation/online_installation/

# nascab
https://hub.docker.com/r/ypptec/nascab
docker run -v /docker/myData:/myData -v /docker/nascabData:/root/.local/share/nascab \
-p 8888:80 -d --log-opt max-size=10m --log-opt max-file=3 ypptec/nascab

# emby 开心特别版
https://hub.docker.com/r/lovechen/embyserver
docker run -d \
--name emby \
--restart unless-stopped \
-p 8096:8096 \
-p 8920:8920 \
-v /docker/emby4714/config:/config \
-v /docker/emby4714/media:/mnt/media \
-v /:/all \
lovechen/embyserver:4.7.14.0

# redis
docker run --name redis -d \
-e REDIS_PASSWORD=密码 \
-p 6379:6379 \
redis:7.2.6 --requirepass 密码
docker exec -it redis redis-cli -a 密码

# mysql

# javaSP java刮削
https://github.com/Yuukiy/JavSP.git
docker run -it -d \
--network="host" \
-v /docker/javsp/media:/media \
-v  /docker/javsp/data:/app/config.yml \
ghcr.io/yuukiy/javsp:master

# ikaros绅士刮削器
https://hub.docker.com/r/suwmlee/ikaros
https://github.com/Suwmlee/ikaros
docker run -d \
--name=ikaros \
-e PUID=0 \
-e PGID=0 \
-e TZ=Asia/Shanghai \
-p 12346:12346 \
-v /path/to/media:/media \
-v /path/to/data:/app/data \
--restart unless-stopped \
suwmlee/ikaros:lates

# Erupt
https://www.erupt.xyz/#!/doc

# kkFileView是一个万能的在线预览开源项目

# MinIO

# r-nacos是一款使用rust实现的nacos服务
https://github.com/nacos-group/r-nacos

docker run --name mynacos -e RNACOS_CONSOLE_ENABLE_CAPTCHA=false \
-e RNACOS_ENABLE_NO_AUTH_CONSOLE=true \
-v /Users/jiaxiaopeng/docker/rnacos/config:/io:rw \
-p 8848:8848 -p 9848:9848 -p 10848:10848 -d qingpan/rnacos:stable

# nginx
docker cp nginx:/etc/nginx/nginx.conf /Users/jiaxiaopeng/docker/nginx/nginx.conf
docker cp nginx:/etc/nginx/conf.d /Users/jiaxiaopeng/docker/nginx/conf.d
docker cp nginx:/usr/share/nginx/html /Users/jiaxiaopeng/docker/nginx/html
docker rm -f nginx

docker run --name nginx -m 200m -p 80:80 \
-v /Users/jiaxiaopeng/docker/nginx/nginx.conf:/etc/nginx/nginx.conf \
-v /Users/jiaxiaopeng/docker/nginx/conf.d:/etc/nginx/conf.d \
-v /Users/jiaxiaopeng/docker/nginx/html:/usr/share/nginx/html \
-v /Users/jiaxiaopeng/docker/nginx/log:/var/log/nginx \
-e TZ=Asia/Shanghai \
--restart=always \
--privileged=true -d nginx