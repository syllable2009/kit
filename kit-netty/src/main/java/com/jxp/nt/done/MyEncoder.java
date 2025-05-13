package com.jxp.nt.done;

import javax.annotation.Resource;

import com.jxp.nt.done.bean.BaseBean;
import com.jxp.nt.done.service.SerializeService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 把消息内容转换成 Byte，也就是说是编码
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:23
 */

@Slf4j
public class MyEncoder extends MessageToByteEncoder<BaseBean> {

    @Resource
    private SerializeService serializeService;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseBean baseBean, ByteBuf byteBuf) throws Exception {
        //1.把“数据”转换成字节数组
        byte[] bytes = serializeService.serialize(baseBean);
        //2.把字节数组往ByteBuf容器写
        byteBuf.writeBytes(bytes);
    }
}
