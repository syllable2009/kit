package com.jxp.nt.dspringboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.nt.done.MyDecoder;
import com.jxp.nt.done.MyEncoder;
import com.jxp.nt.done.client.MsgBaseBeanHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 11:21
 */
@Configuration
@EnableConfigurationProperties
public class NettyClientConfig {
    private NettyProperties nettyProperties;

    public NettyClientConfig(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public Channel channel;

    @Bean(name = "clientGroup", destroyMethod = "shutdownGracefully")
    public EventLoopGroup clientGroup() {
        return new NioEventLoopGroup(2);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Bean
    public Bootstrap clientBootstrap(EventLoopGroup clientGroup) {
        return new Bootstrap()
                .group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 5, 4))
                                .addLast(new MyDecoder())
                                .addLast(new MsgBaseBeanHandler())
                                .addLast(new MyEncoder());
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    }
}
