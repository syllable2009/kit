package com.jxp.nt.done.handler;

import com.jxp.nt.done.bean.MsgReqBean;
import com.jxp.nt.done.bean.MsgResBean;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:34
 */
@Slf4j
public class MsgReqHandler extends SimpleChannelInboundHandler<MsgReqBean> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgReqBean msgReqBean) throws Exception {
        //消息发送逻辑
        log.info("MsgReqHandler,msgReqBean:{}", JSONUtil.toJsonStr(msgReqBean));
        ctx.channel().writeAndFlush(MsgResBean.builder()
                .status(0)
                .build());
    }
}
