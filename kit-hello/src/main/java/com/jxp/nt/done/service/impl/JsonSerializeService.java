package com.jxp.nt.done.service.impl;

import com.jxp.nt.done.service.SerializeService;

/**
 * 具体json序列化实现列
 * @author jiaxiaopeng
 * Created on 2025-05-09 15:32
 */
public class JsonSerializeService<T> implements SerializeService<T> {
    //序列化方法
    public byte[] serialize(T t) {
        return null;
    }

    //反序列化方法
    public T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
