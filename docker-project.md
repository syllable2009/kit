## 收集的docker项目

# 一款实用的个人IT工具箱——it-tools
https://hub.docker.com/r/corentinth/it-tools
开源项目：https://github.com/CorentinTh/it-tools
docker run -d --name it-tools --restart unless-stopped -p 8800:80 corentinth/it-tools:latest

# 阅读3网页版
Reading：免费小说阅读 app，支持多种源与 TTS，提供多样阅读体验。
https://hub.docker.com/r/hectorqin/reader
开源项目：https://github.com/celetor/web-yuedu3
docker run -d --restart=always \
--name=reader -e "SPRING_PROFILES_ACTIVE=prod" \
-v /docker/reader/logs:/logs -v /docker/reader/storage:/storage \
-p 9800:8080 hectorqin/reader:3.2.11

# Teemii
Teemii：实现在线漫画下载与管理，满足漫画爱好者需求。
git clone https://github.com/dokkaner/teemii.git
cd ../server
docker build -t teemii-backend .
docker run -d --name teemii-backend --network teemii-network -v teemii-data:/app/data teemii-backend
cd ../app
docker build -t teemii-frontend .
docker run -d -p 8080:80 --name teemii-frontend --network teemii-network teemii-frontend

# 1panel
https://1panel.cn/docs/installation/online_installation/
docker run -d \
--name 1panel \
-p 10086:10086 \
--restart always \
--network host \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /Users/jxp/docker/debian/volumes:/var/lib/docker/volumes \
-v /Users/jxp/docker/debian/opt:/opt \
-v /Users/jxp/docker/debian/root:/root \
-v /Users/jxp/docker/debian/home:/home \
-v /Users/jxp/docker/debian/var:/var \
-e TZ=Asia/Shanghai \
moelin/1panel:latest

http://jxp:10086/entrance

# nascab
https://hub.docker.com/r/ypptec/nascab
docker run -v /Users/jiaxiaopeng/docker/nascabData:/root/.local/share/nascab \
-p 8888:80 -p 5555:90 \
--name nascab \
--network host \
-v /mnt:/mnt \
-v /media:/media \
-v /Volumes:/Volumes \
-d --log-opt max-size=10m --log-opt max-file=3 ypptec/nascab:3.5.3-arm64

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
docker run --name mysql8 -v /Users/jxp/docker/mysql8/conf:/etc/mysql/conf.d \
-v /Users/jxp/docker/mysql8/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=admin123456 -p 3306:3306 -d 
mysql:8

CREATE USER 'root'@'%' IDENTIFIED BY 'admin123456'; 创建一个新的 root 用户
ALTER USER 'root'@'%' IDENTIFIED BY 'admin123456';           -- 修改任何 IP 用户的密码
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;           -- 任何 IP
FLUSH PRIVILEGES;  -- 刷新权限

CREATE DATABASE erupt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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
-v /Users/jxp/docker/rnacos/config:/io:rw \
-p 8848:8848 -p 9848:9848 -p 10848:10848 -d qingpan/rnacos:stable

# nginx
docker cp nginx:/etc/nginx/nginx.conf /Users/jxp/docker/nginx/nginx.conf
docker cp nginx:/etc/nginx/conf.d /Users/jxp/docker/nginx/conf.d
docker cp nginx:/usr/share/nginx/html /Users/jxp/docker/nginx/html
docker rm -f nginx

docker run --name nginx -m 200m -p 80:80 \
-v /Users/jxp/docker/nginx/nginx.conf:/etc/nginx/nginx.conf \
-v /Users/jxp/docker/nginx/conf.d:/etc/nginx/conf.d \
-v /Users/jxp/docker/nginx/html:/usr/share/nginx/html \
-v /Users/jxp/docker/nginx/log:/var/log/nginx \
-e TZ=Asia/Shanghai \
--restart=always \
--privileged=true -d nginx


# liteflow
LiteFlow是一个轻量且强大的国产规则引擎框架
# AviatorScript
AviatorScript 是一门高性能、轻量级寄宿于 JVM （包括 Android 平台）之上的脚本语言。
# radar开源的风控项目
https://gitee.com/freshday/radar


# 1panel
docker run -d \
--name 1panel \
-p 10086:10086 \
--restart always \
--network host \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /Users/jxp/docker/1panel/volumes:/var/lib/docker/volumes \
-v /Users/jxp/docker/1panel/opt:/opt \
-v /Users/jxp/docker/1panel/root:/root \
-v /Users/jxp/docker/1panel/home:/home \
-e TZ=Asia/Shanghai \
moelin/1panel:latest

http://jxp:10086/entrance

# LocalSend
LocalSend 是一个自由、开源的应用程序，允许你在本地网络上安全地与附近设备分享文件和消息，无需互联网连接。
https://github.com/localsend/localsend

# input-leap
Input Leap 是一款模拟 KVM 切换器功能的软件，从历史上看，KVM 切换器允许您使用单个键盘和鼠标来控制多台计算机。
所有共享键盘和鼠标的机器上都需要安装 Input Leap。
https://github.com/input-leap/input-leap

# memos
一款清爽的轻量级备忘录中心。
https://github.com/usememos/memos
docker run -d --name memos -p 5230:5230 -v /Users/jiaxiaopeng/docker/memos/:/var/opt/memos neosmemo/memos:stable

# Melody
网易云-音乐精灵，旨在帮助你更好地管理音乐。目前的主要能力是帮助你将喜欢的歌曲或者音频上传到音乐平台的云盘。
docker run -d -p 5566:5566 --name melody -v /Users/jiaxiaopeng/Music:/Users/jiaxiaopeng/Music -v \
/Users/jiaxiaopeng/docker/melody/melody-profile:/app/backend/.profile foamzou/melody:latest

# music-tag-web
https://github.com/xhongc/music-tag-web
docker run -d -p 8002:8002 -v /path/to/your/music:/app/media -v /path/to/your/config:/app/data --restart=always xhongc/music_tag_web:latest

# Musicn：用于下载 mp3 音乐，方便用户获取所需音频文件
https://github.com/zonemeen/musicn

# Termux
Termux 是一款Android 终端模拟器和 Linux 环境应用，无需 root 权限或设置即可直接使用
https://termux.dev/en/

# UserLAnd
在 Android 上运行 Linux 发行版或应用程序的最简单方法。
https://github.com/CypherpunkArmory/UserLAnd