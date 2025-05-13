package com.jxp.nt.done;

import java.util.List;

import javax.annotation.Resource;

import com.jxp.nt.done.bean.BaseBean;
import com.jxp.nt.done.service.SerializeService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 把ByteBuf反序列化，并且添加到List里面
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:25
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {

    @Resource
    private SerializeService<BaseBean> serializeService;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //1.定义byte[]，长度为ByteBuf可读长度
        byte[] bytes = new byte[byteBuf.readableBytes()];
        //2.往byte[]读取数据
        byteBuf.readBytes(bytes);
        final BaseBean bean = serializeService.deserialize(bytes, BaseBean.class);
//        //5.添加到集合
        list.add(bean);
    }
}
