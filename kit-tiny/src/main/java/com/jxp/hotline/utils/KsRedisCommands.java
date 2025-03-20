package com.jxp.hotline.utils;

import java.util.Map;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 17:06
 */
public interface KsRedisCommands<K, V> {

    Long del(K var1);

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


    @Data
    static class SetArgs {
        private Long ex;
        private Long px;
        private boolean nx = false;
        private boolean xx = false;
    }
}
