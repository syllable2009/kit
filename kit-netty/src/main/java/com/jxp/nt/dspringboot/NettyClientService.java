package com.jxp.nt.dspringboot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxp.nt.done.bean.BaseBean;

import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 14:16
 */
@Slf4j
@Service
public class NettyClientService {

    private NettyProperties nettyProperties;

    public NettyClientService(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }
    @Resource
    private Bootstrap clientBootstrap;

    private Channel channel;

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @PostConstruct
    public void init() {
        connectWithRetry(3, 5000);
    }

    // 发送消息方法
    public void sendMessage(BaseBean bean) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(bean);
            System.out.println("消息发送成功: " + JSONUtil.toJsonStr(bean));
        } else {
            System.out.println("连接未建立，消息发送失败:" + JSONUtil.toJsonStr(bean));
        }
    }

    private void connectWithRetry(int maxRetry, long interval) {
        for (int i = 0; i < maxRetry; i++) {
            try {
                ChannelFuture future = clientBootstrap.connect(nettyProperties.getHost(), nettyProperties.getPort()).sync();
                if (future.isSuccess()) {
                    this.channel = future.channel();
                    log.info("成功连接到服务端 {}:{}", nettyProperties.getPort(), nettyProperties.getPort());
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.error("连接服务端失败，已达最大重试次数");
    }
}
