# 配置文件

一般一个域名为一个配置文件,命名为mrsingsing.com.8080.conf
为了使 Nginx 配置更易于维护，建议为每个服务创建一个单独的配置文件，存储在 /etc/nginx/conf.d 目录


# 命令行指令

### 测试发布
docker exec -it <nginx_container_name_or_id> /bin/bash
### 立即停止服务
nginx -s stop
### 优雅地停止服务
nginx -s quit
### 测试配置文件是否有语法错误，同时显示主配置文件路径
nginx -t
### 检查指定的配置文件
nginx -t -c /etc/nginx/nginx.conf
### 重载配置文件
nginx -s reload
### 重新开始记录日志文件
nginx -s reopen

# 配置语法
Nginx 配置的核心是定义要处理的 URL 以及如何响应这些 URL 请求。
即定义一系列的 虚拟服务器（Virtual Servers） 控制对来自特定域名或者 IP 的请求的处理。
每一个虚拟服务器定义一系列的 location 控制处理特定的 URI 集合。每一个 location 定义了对映射到自己的请求的处理场景，可以返回一个文件或者代理此请求。
### main 全局块
### Events 块
events {
}
### Http 块,配置使用最频繁的部分，代理、缓存、日志定义等绝大多数功能和第三方模块的配置都在这里设置
http {
    ### Server 块
    server {
        ### Location 块
        location [PATTERN] {}
        location [PATTERN] {}
    }
}
main：Nginx 的全局配置，对全局生效
events：配置影响 Nginx 服务器或与用户的网络连接
http：可以嵌套多个 Server，配置代理、缓存、日志定义等绝大多数功能和第三方模块的配置
server：配置虚拟主机的相关参数，一个 HTTP 中可以有多个 server 块
location：用于配置请求的路由，以及各种页面的处理情况
upstream：配置后端服务器具体地址，负载均衡配置不可或缺的部分

每条指令以 ; 分号结尾
指令块以 {} 大括号将多条指令组织在一起
include 语句允许组合多个配置文件以提升可维护性
使用 # 符号添加注释，提高可读性
使用 $ 符号使用变量
部分指令的参数支持正则表达式

upstream 为 Nginx 内置模块，用于定义上游服务器（指的是后台提供的应用服务器）的相关信息。
upstream back_end_server {
server 192.168.100.33:8081;
}

server_name:指定虚拟主机域名,匹配优先级： 精准匹配 > 左侧通配符匹配 > 右侧通配符匹配 > 正则表达式匹配
精确匹配：server_name www.nginx.com
左侧通配：server_name *.nginx.com
右侧通配：server_name www.nginx.*
正则匹配：server_name ~^www\.nginx\.*$


location 块：配置请求的路由，以及各种页面的处理情况。
location 配置
请求根目录配置
更改 location 的 URI
网站默认首页配置

location /test/ {
...
}
不带 / 当访问 www.nginx-test.com/test 时，Nginx 先找是否有 test 目录，如果有则找 test 目录下的 index.html；如果没有 test 目录，Nginx 则会找是否有 test 文件
带 / 当访问 www.nginx-test.com/test 时，Nginx 先找是否有 test 目录，如果有则找 test 目录下的 index.html；如果没有它也不会去找是否存在 test 文件


root指定静态资源目录位置，它可以写在 http、servr、location 块等配置中。
rewrite 根据指定正则表达式匹配规则，重写 URL。
return 停止处理请求，直接返回响应码或重定向到其他 URL；执行 return 指令后，location 中后续指令讲不会被执行。

proxy_pass 用于配制代理服务器
proxy_pass http://erupt;
proxy_pass http://erupt/;
这两种用法的区别就是带 / 和不带 /，在配置代理时它们的区别可大了：
不带 / 意味着 Nginx 不会修改用户 URL，而是直接透传给上游的应用服务器
带 / 意味着 Nginx 会修改用户 URL，修改方案是将 location 后的 URL 从用户 URL 中删除

listen  80;
server_name www.nginx-test.com;
// 只有当访问 www.nginx-test.com/match_all/ 时才会匹配到/usr/share/nginx/html/match_all/index.html
location = /match_all/ {
    rewrite ^/8081(.*)$ $1 break;
    root  /usr/share/nginx/html
    index index.html
}



http {
    upstream api.mrsingsing {
    # 采用 ip_hash 负载均衡策略
    ip_hash;
    # 采用 fair 负载均衡策略
    # fair;
    # 负载均衡目的服务地址
    server 127.0.0.1:8081 backup;
    server 127.0.0.1:8080;
    server 127.0.0.1:8082 weight=10;  # weight 方式，不写默认为 1
    }
    server {
        location / {
        proxy_pass http://api.mrsingsing;
        proxy_connect_timeout 10;
        }
    }
}

