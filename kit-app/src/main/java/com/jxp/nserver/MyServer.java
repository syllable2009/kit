package com.jxp.nserver;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 在Netty中，一个请求会创建一个Channel通道，childHandler是具体处理请求的处理器。
 * 每个Channel只有一个ChannelPipeline。ChannelPipeline是ChannelHandler的容器，它负责ChannelHandler的管理和事件拦截。该pipeline在Channel被创建的时候创建。
 * ChannelPipeline包含了一个ChannelHander形成的列表，且所有ChannelHandler都会注册到ChannelPipeline中。Pipeline是一个双向链表结构。
 * Netty的ChannelPipeline和ChannelHandler机制类似于Servlet和Filter过滤器。
 * HeadContext<=>InboundHandler1<=>InboundHandler2<=>InboundHandler3<=>OutboundHandler1
 * <=>OutboundHandler2<=>OutboundHandler3<=>ExceptionHandler<=>TailContext
 * 无论是inboundHandler或者是outboundHandler的异常还是write，都是按序向tail方向传递的。所以异常处理添加到tail方向的末尾。
 *
 *
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
                            socketChannel.pipeline()
                                    .addLast(new HttpServerCodec())
                                    // http 消息聚合器1024*1024为接收的最大contentlength
                                    .addLast(new HttpObjectAggregator(1024 * 1024))
                                    .addLast(new HttpRequestHandler())
                                    .addLast(new ExceptionHandler());
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

    @Slf4j
    static class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            if (is100ContinueExpected(req)) {
                // 检测 100 Continue，是否同意接收将要发送过来的实体
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            // 因为经过HttpServerCodec处理器的处理后消息被封装为FullHttpRequest对象
            ByteBuf buf = req.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            log.info("req result:{}", result);
            // 获取请求的uri
            Map map = new HashMap<>();
            map.put("method", req.method().name()); // 获取请求方法
            map.put("uri", req.uri()); // 获取请求地址
            // 创建完整的响应对象
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.copiedBuffer(JSONUtil.toJsonStr(map),
                    CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            // 响应写回给客户端,并在协会后断开这个连接
//            ctx.write(1 / 0);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    static class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
        private final ConcurrentHashMap<String, Channel> loggedInUsers;

        public WebSocketFrameHandler(ConcurrentHashMap<String, Channel> loggedInUsers) {
            this.loggedInUsers = loggedInUsers;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
            if (frame instanceof TextWebSocketFrame) {
                String request = ((TextWebSocketFrame) frame).text();
                System.out.println("Received: " + request);

                // 处理登录请求
                if (request.startsWith("LOGIN:")) {
                    String[] parts = request.split(":");
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];
                        // 进行用户名和密码的验证
                        if (authenticate(username, password)) {
                            loggedInUsers.put(username, ctx.channel());
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("Login successful: " + username));
                        } else {
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("Login failed: Invalid credentials"));
                        }
                    } else {
                        ctx.channel().writeAndFlush(new TextWebSocketFrame("Login failed: Invalid format"));
                    }
                } else {
                    // 检查用户是否已登录
                    String username = getUsernameFromChannel(ctx.channel());
                    if (username != null) {
                        // 处理已登录用户的其他请求
                        String response = "Response from " + username + ": " + request;
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(response));
                    } else {
                        ctx.channel().writeAndFlush(new TextWebSocketFrame("Please login first"));
                    }
                }
            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("Received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                ctx.close();
            } else if (frame instanceof PingWebSocketFrame) {
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            } else {
                throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
            }
        }

        private boolean authenticate(String username, String password) {
            // 实现您的认证逻辑，这里使用简单的示例
            return "admin".equals(username) && "password".equals(password);
        }

        private String getUsernameFromChannel(Channel channel) {
            for (String username : loggedInUsers.keySet()) {
                if (loggedInUsers.get(username) == channel) {
                    return username;
                }
            }
            return null;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
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
