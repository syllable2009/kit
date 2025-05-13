package com.jxp.nt.dspringboot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.nt.done.MyDecoder;
import com.jxp.nt.done.MyEncoder;
import com.jxp.nt.done.handler.LoginReqHandler;
import com.jxp.nt.done.handler.MsgReqHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
    public NioEventLoopGroup bossGroup() {
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
    public ServerBootstrap serverBootstrap(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                // 指定使用的线程组
                .group(bossGroup, workerGroup)
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
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 5, 4));
                        pipeline.addLast(new MyDecoder());
                        pipeline.addLast(new LoginReqHandler());
                        pipeline.addLast(new MsgReqHandler());
                        pipeline.addLast(new MyEncoder());
                    }
                });
        return serverBootstrap;
    }

}
