package com.jxp.nt.done.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-08 11:15
 */
public class NettyClient {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 1.指定线程模型
                .group(workerGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                // 3.给客户端 Channel 指定处理逻辑 Handler
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        //自定义业务 Handler
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
        // 4.建立连接
        ChannelFuture future = bootstrap.connect("127.0.0.1", 80).sync();

        //2.监听连接结果
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("连接成功!");
            } else {
                System.err.println("连接失败!");
                //递归调用连接方法
                connect(bootstrap, "127.0.0.1", 8080);
            }
        });
    }

    private static void connect(Bootstrap bootstrap, String host, int port) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else {
                //获取EventLoopGroup
                EventLoopGroup thread = bootstrap.config().group();
                //每隔5秒钟重连一次
                thread.schedule(new Runnable() {
                    public void run() {
                        connect(bootstrap, host, port);
                    }
                }, 3, TimeUnit.SECONDS);
            }
        });
    }
}
