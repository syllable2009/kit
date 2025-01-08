package com.jxp.nserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

/**
 * 在Netty中，一个请求会创建一个Channel通道，childHandler是具体处理请求的处理器。
 * 每个Channel只有一个ChannelPipeline。ChannelPipeline是ChannelHandler的容器，它负责ChannelHandler的管理和事件拦截。该pipeline在Channel被创建的时候创建。
 * ChannelPipeline包含了一个ChannelHander形成的列表，且所有ChannelHandler都会注册到ChannelPipeline中。Pipeline是一个双向链表结构。
 * Netty的ChannelPipeline和ChannelHandler机制类似于Servlet和Filter过滤器。
 * @author jiaxiaopeng
 * Created on 2025-01-07 15:53
 */
public class MyServer {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) throws Exception {
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数，也就是boosGroup线程
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态，也就是workerGroup线程
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象，childHandler是具体处理请求的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器，ChannelPipeline是Netty处理请求的责任链
                            socketChannel.pipeline().addLast(new MyServerHandler());
                        }
                    });
            //给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("java技术爱好者的服务端已经准备就绪...");
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(6666).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    static class MyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //获取客户端发送过来的消息
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("收到客户端" + ctx.channel().remoteAddress() + "发送的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //发送消息给客户端
            ctx.writeAndFlush(Unpooled.copiedBuffer("服务端已收到消息，并给你发送一个问号?", CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //发生异常，关闭通道
            ctx.close();
        }
    }


    public static void example() {
        ByteToMessageDecoder byteToMessageDecoder;
        MessageToMessageDecoder messageToMessageDecoder;

        // 这个解码器用于处理拆包和粘包问题。它通过读取消息的长度字段来确定每个消息的边界。
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder;
        MessageToByteEncoder messageToByteEncoder;
        MessageToMessageEncoder messageToMessageEncoder;
        // 这个编码器在发送消息之前会在消息前添加长度字段，确保接收方能够正确解析消息。
        LengthFieldPrepender lengthFieledPrepender;
    }
}
