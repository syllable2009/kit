package com.jxp.nt.done.service;

/**
 * 定义一个序列化接口
 * @author jiaxiaopeng
 * Created on 2025-05-09 15:31
 */
public interface SerializeService<T> {
    //序列化方法
    byte[] serialize(T t);

    //反序列化方法
    T deserialize(byte[] bytes, Class<T> clazz);
}
