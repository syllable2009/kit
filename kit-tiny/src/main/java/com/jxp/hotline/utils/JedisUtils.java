package com.jxp.hotline.utils;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.jxp.hotline.utils.JedisCommands.SetArgs;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 17:05
 */
@Slf4j
public class JedisUtils {
    private static final int DEFAULT_ACQUIRE_TIMEOUT = 3; // 默认请求超时3秒
    private static final int DEFAULT_LOCK_EXPIRE = 10;    // 默认锁过期10秒
    private static final int DEFAULT_RETRY_INTERVAL = 10;    // 默认重试间隔10毫秒


    // 加锁，时间单位为秒
    public static String tryLock(JedisCommands<String, String> jedisCommands,
            String lockKey, long waitTime, long expireTime) {
        String requestId = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();

        // 不等待直接返回
        if (waitTime <= 0) {
            final SetArgs setArgs = new SetArgs();
            setArgs.setNx(true);
            setArgs.setEx(expireTime);
            final String result = jedisCommands.set(lockKey, requestId,
                    setArgs);
            if ("OK".equals(result)) {
                return requestId;
            }
            return null;
        }


        long endTime = System.currentTimeMillis() + waitTime * 1000;
        final SetArgs setArgs = new SetArgs();
        setArgs.setNx(true);
        setArgs.setEx(expireTime);
        while (currentTime < endTime) {
            final String result = jedisCommands.set(lockKey, requestId,
                    setArgs);
            if ("OK".equals(result)) {
                return requestId;
            }
            currentTime = currentTime + DEFAULT_RETRY_INTERVAL;
            try {
                // 降低重试频率
                Thread.sleep(DEFAULT_RETRY_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    public static String tryLock(JedisCommands<String, String> jedisCommands, String lockKey) {
        return tryLock(jedisCommands, lockKey, DEFAULT_ACQUIRE_TIMEOUT, DEFAULT_LOCK_EXPIRE);
    }

    // 存在并发问题，删除和获取不是原子操作
    public static void releaseLock(JedisCommands<String, String> jedisCommands, String lockKey,
            String requestId) {
        if (StrUtil.isBlank(requestId)) {
            return;
        }
        String value = jedisCommands.get(lockKey);
        if (StrUtil.isBlank(value)) {
            return;
        }
        if (requestId.equals(value)) {
            jedisCommands.del(lockKey);
        }
    }

    public static void releaseLockSafe(JedisCommands<String, String> jedisCommands, String lockKey,
            String requestId) {
        if (StrUtil.isBlank(requestId)) {
            return;
        }
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', "
                + "KEYS[1]) == 1 and true or false else return false end";
//        jedisCommands.eval(script, ScriptOutputType.BOOLEAN, new String[]{lockKey},
//                requestId);
    }

    public static Integer incr(JedisCommands<String, String> jedisCommands, String key) {
        // 原子性加一，如果该键不存在，Redis 会将其初始化为 0，然后执行增加操作，所以结果为 1
        return jedisCommands.incr(key).intValue();
    }

    public static Integer decr(JedisCommands<String, String> jedisCommands, String key) {
        // 原子性减一，如果key 不存在，那么key 的值会先被初始化为0 ，然后再执行DECR 操作
        return jedisCommands.decr(key).intValue();
    }

    public static Integer getInt(JedisCommands<String, String> jedisCommands, String key) {
        // 原子性减一，如果key 不存在，那么key 的值会先被初始化为0 ，然后再执行DECR 操作
        final String num = jedisCommands.get(key);
        if (StrUtil.isBlank(num)) {
            return 0;
        }
        return Integer.parseInt(num);
    }

    public static String get(JedisCommands<String, String> jedisCommands, String key) {
        return jedisCommands.get(key);
    }

    public static String setex(JedisCommands<String, String> jedisCommands, String key, long expire, String value) {
        return jedisCommands.setex(key, expire, value);
    }

    public static <T> T lockExec(JedisCommands<String, String> ksRedisCommands, String lockKey, Callable<T> method,
            Consumer<T> onSuccess,
            Consumer<Exception> onException) {
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("lockExec return,lock fail,lockKey:{}, method:{}", lockKey, method.getClass());
            return null;
        }
        try {
            final T ret = method.call();
            if (onSuccess != null) {
                onSuccess.accept(ret);
            }
            return ret;
        } catch (Exception e) {
            log.error("lockExec error,lockKey:{}, method:{}, e", lockKey, method.getClass(), e);
            if (onException != null) {
                onException.accept(e);
            }
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
        return null;
    }
}
