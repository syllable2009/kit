package com.jxp.component.jedis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-03 11:09
 */

@ConditionalOnProperty(prefix = "jedis", name = "enable", havingValue = "true")
@Configuration
public class JedisConfig {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // 最大连接数
        poolConfig.setMaxIdle(128); // 最大空闲连接
        poolConfig.setMinIdle(16); // 最小空闲连接
        poolConfig.setTestOnBorrow(true); // 在获取连接时检查有效性
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisPool(poolConfig, "localhost", 6379, 2000, "admin1234"); // 设置密码
    }
}
