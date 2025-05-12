package com.jxp.nt.dspringboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-09 16:30
 */
@Configuration
@EnableConfigurationProperties
public class NettyConfig {

    private NettyProperties nettyProperties;

    public NettyConfig(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    /**
     * boss线程池-进行客户端连接
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyProperties.getBoss());
    }

    /**
     * worker线程池-进行业务处理
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorker());
    }

    /**
     * 服务端启动器，监听客户端连接
     *
     * @return
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                // 指定使用的线程组
                .group(boosGroup(), workerGroup())
                // 指定使用的通道
                .channel(NioServerSocketChannel.class)
                // 指定tcp连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getTimeout())
                // 指定worker处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 使用自定义处理拆包/沾包，并且每次查找的最大长度为1024字节
//                        pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
//                        // 将上一步解码后的数据转码为Message实例
//                        pipeline.addLast(new MessageDecodeHandler());
//                        // 对发送客户端的数据进行编码，并添加数据分隔符
//                        pipeline.addLast(new MessageEncodeHandler(delimiterStr));
//                        // 对数据进行最终处理
//                        pipeline.addLast(new ServerListenerHandler());
                    }
                });
        return serverBootstrap;
    }

}
