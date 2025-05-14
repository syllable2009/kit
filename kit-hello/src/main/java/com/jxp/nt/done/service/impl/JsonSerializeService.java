package com.jxp.nt.done.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.nt.done.service.SerializeService;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 具体json序列化实现列
 * @author jiaxiaopeng
 * Created on 2025-05-09 15:32
 */
@Slf4j
@Service
public class JsonSerializeService<T> implements SerializeService<T> {
    //序列化方法
    public byte[] serialize(T t) {
        return ObjectUtil.serialize(t);
    }

    //反序列化方法
    public T deserialize(byte[] bytes, Class<T> clazz) {
        return ObjectUtil.deserialize(bytes);
    }
}
