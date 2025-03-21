package com.jxp.hotline.utils;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 17:06
 */
public interface KsRedisCommands<K, V> {

    Long del(K... var1);

    Long append(K var1, V var2);

    Long bitcount(K var1);

    Long bitcount(K var1, long var2, long var4);


    Long bitpos(K var1, boolean var2);

    Long bitpos(K var1, boolean var2, long var3);

    Long bitpos(K var1, boolean var2, long var3, long var5);

    Long bitopAnd(K var1, K... var2);

    Long bitopNot(K var1, K var2);

    Long bitopOr(K var1, K... var2);

    Long bitopXor(K var1, K... var2);

    Long decr(K var1);

    Long decrby(K var1, long var2);

    V get(K var1);

    Long getbit(K var1, long var2);

    V getrange(K var1, long var2, long var4);

    V getset(K var1, V var2);

    Long incr(K var1);

    Long incrby(K var1, long var2);

    Double incrbyfloat(K var1, double var2);

    String mset(Map<K, V> var1);

    Boolean msetnx(Map<K, V> var1);

    String set(K var1, V var2);

    String set(K var1, V var2, SetArgs var3);

    Long setbit(K var1, long var2, int var4);

    String setex(K var1, long var2, V var4);

    String psetex(K var1, long var2, V var4);

    Boolean setnx(K var1, V var2);

    Long setrange(K var1, long var2, V var4);

    Long strlen(K var1);

    V brpoplpush(long var1, K var3, K var4);

    V lindex(K var1, long var2);

    Long linsert(K var1, boolean var2, V var3, V var4);

    Long llen(K var1);

    V lpop(K var1);

    Long lpush(K var1, V... var2);

    Long lpushx(K var1, V... var2);

    List<V> lrange(K var1, long var2, long var4);

    Long lrem(K var1, long var2, V var4);

    String lset(K var1, long var2, V var4);

    String ltrim(K var1, long var2, long var4);

    V rpop(K var1);

    V rpoplpush(K var1, K var2);

    Long rpush(K var1, V... var2);

    // 如果key不存在，什么都不做
    Long rpushx(K var1, V... var2);

    Long exists(K... var1);

    Boolean expire(K var1, long var2);

    @Data
    static class SetArgs {
        private Long ex;
        private Long px;
        private boolean nx = false;
        private boolean xx = false;
    }
}
