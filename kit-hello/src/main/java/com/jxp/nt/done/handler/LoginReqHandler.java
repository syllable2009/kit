package com.jxp.nt.done.handler;

import org.apache.commons.lang3.StringUtils;

import com.jxp.nt.done.bean.LoginReqBean;
import com.jxp.nt.done.bean.LoginResBean;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:33
 */
@Slf4j
public class LoginReqHandler extends SimpleChannelInboundHandler<LoginReqBean> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginReqBean loginReqBean) throws Exception {
        //登录逻辑
        log.info("LoginReqHandler,loginReqBean:{}", JSONUtil.toJsonStr(loginReqBean));
        if (null != loginReqBean && StringUtils.isNotBlank(loginReqBean.getUserId())) {
            //1.登录成功，则给通道绑定属性
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginReqBean.getUserId());
            //2.调用发送消息方法
            ctx.channel().writeAndFlush(LoginResBean.builder()
                    .status(0)
                    .msg("登录成功")
                    .build());
        } else {
            ctx.channel().writeAndFlush(LoginResBean.builder()
                    .status(-1)
                    .msg("登录失败")
                    .build());
        }
    }
}
