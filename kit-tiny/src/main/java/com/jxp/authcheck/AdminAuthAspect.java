package com.jxp.authcheck;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-02 12:49
 */
@Slf4j
@Aspect
@Component
public class AdminAuthAspect {


    @SuppressWarnings("checkstyle:LineLength")
    private static String ADMIN_SALT =
            "2cd7a77a9cc74265b46a827ebcad5d4333d41e041ac64a6fb99b3d9a047fdb052c310207f2794cd7a2f3f6fa2c7f9c150539318056d14993b111caf46c580b92";

    @Before("@within(adminAuthCheck) || @annotation(adminAuthCheck)")
    public void adminAuthAspect(JoinPoint joinPoint, AdminAuthCheck adminAuthCheck) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 检查方法上的注解
        AdminAuthCheck authCheck = method.getAnnotation(AdminAuthCheck.class);
        if (authCheck == null) {
            // 检查类上的注解
            authCheck = joinPoint.getTarget().getClass().getAnnotation(AdminAuthCheck.class);
        }
        if (null == authCheck) {
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();

        final String operator = getValueFromRequest(request, "operator");
        final String sign = getValueFromRequest(request, "sign");

        final Object[] args = joinPoint.getArgs();

        log.info("adminAuthAspect,operator:{},method:{},params:{}", operator, method.getName(), JSONUtil.toJsonStr(args));
        if (StrUtil.hasBlank(operator, sign)) {
            throw new RuntimeException("参数错误");
        }
        // 一月一个秘钥
        final String md5Sign = getMd5Sign(operator, ADMIN_SALT, DateUtil.format(new DateTime(), "yyyy-MM"));
        if (StrUtil.equals(sign, md5Sign)) {
            return;
        }
        throw new RuntimeException("无权限");
    }

    private static String getValueFromRequest(HttpServletRequest request, String paramName) {
        if (null == request) {
            return null;
        }
        String value = request.getHeader(paramName); // 优先从请求头获取
        if (value == null) {
            value = request.getParameter(paramName); // 从请求参数获取
        }
        return value;
    }

    private static String getMd5Sign(String... params) {
        String collect = Stream.of(params)
                .collect(Collectors.joining("|"));
        return SecureUtil.md5(collect);
    }
}
