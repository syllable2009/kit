package com.jxp.nt.dspringboot;

import org.springframework.boot.CommandLineRunner;
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

/**
 * @author jiaxiaopeng
 * Created on 2025-05-09 16:30
 */
@Configuration
@EnableConfigurationProperties
public class NettyServerConfig {

    private NettyProperties nettyProperties;

    public NettyServerConfig(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    /**
     * boss线程池-进行客户端连接
     *
     * @return
     */
    @Bean(name = "serverBossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup serverBossGroup() {
        return new NioEventLoopGroup(nettyProperties.getBoss());
    }

    /**
     * worker线程池-进行业务处理
     *
     * @return
     */
    @Bean(name = "serverWorkerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup serverWorkerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorker());
    }

    /**
     * 服务端启动器，监听客户端连接
     *
     * @return
     */
    @Bean
    public ServerBootstrap serverBootstrap(NioEventLoopGroup serverBossGroup, NioEventLoopGroup serverWorkerGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                // 指定使用的线程组
                .group(serverBossGroup, serverWorkerGroup)
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
//                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 5, 4));
                        pipeline.addLast(new MyDecoder());
                        pipeline.addLast(new LoginReqHandler());
                        pipeline.addLast(new MsgReqHandler());
                        pipeline.addLast(new MyEncoder());
                    }
                });
        return serverBootstrap;
    }

    // 服务端启动
    @Bean
    public CommandLineRunner serverRunner(ServerBootstrap serverBootstrap) {
        return args -> {
            // 绑定端口启动
            // 备用端口
            serverBootstrap.bind(nettyProperties.getHost(), nettyProperties.getPort()).sync().addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("服务启动成功!");
                } else {
                    System.err.println("服务启动失败!");
                    //递归延迟重启N次
                }
            });
            System.out.println("Netty服务端已启动，监听端口：" + nettyProperties.getPort());
        };
    }

}
