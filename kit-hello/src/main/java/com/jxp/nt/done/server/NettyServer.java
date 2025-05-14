package com.jxp.nt.done.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-08 11:16
 */
public class NettyServer {


    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) {
        //线程组-主要是监听客户端请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //线程组-主要是处理具体业务
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                //指定线程组
                .group(bossGroup, workerGroup)
                //指定 NIO 模式
                .channel(NioServerSocketChannel.class)
                //双向链表管理
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        //1.解码器
                        //2.编码器
                        //责任链，指定自定义处理业务的 Handler
                        ch.pipeline()
                                .addLast()
                                .addLast()
                                .addLast(new NettyServerHandler());
                    }
                }).handler(new ChannelInitializer<NioServerSocketChannel>() {
                    // handler () 用于指定在服务端启动过程中的一些逻辑。
                    protected void initChannel(NioServerSocketChannel ch) {
                        System.out.println("服务端启动中");
                    }
                });
        //绑定端口号
        serverBootstrap.bind(80);
    }
}
