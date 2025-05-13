package com.jxp.nt.done.client;

import com.jxp.nt.done.bean.BaseBean;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:34
 */
@Slf4j
public class MsgBaseBeanHandler extends SimpleChannelInboundHandler<BaseBean> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseBean bean) throws Exception {
        //消息发送逻辑
        log.info("MsgBaseBeanHandler,bean:{}", JSONUtil.toJsonStr(bean));
        ctx.channel().writeAndFlush(bean);
    }
}
