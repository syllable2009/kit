package com.jxp.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jxp.response.Result;
import com.jxp.resultcode.CommonResultCode;
import com.jxp.resultcode.ResultCode;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-29 15:07
 */

@Order(FilterOrder.FOUR)
@Component
@Slf4j
public class AuthPermissionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthPermissionInterceptor start");
        super.preHandle(request, response, handler);
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (isAllowAnonymous(handler)) {
            setErrorResponse(response, CommonResultCode.NO_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        log.info("AuthPermissionInterceptor end");
    }

    private boolean isAllowAnonymous(Object handler) {
        return !isMethodHasAnnotation(handler, AllowAnonymous.class);
    }

    private boolean isAuthRequired(Object handler) {
        return !isMethodHasAnnotation(handler, IgnoreAuth.class);
    }

    private boolean isMethodHasAnnotation(Object handler, Class<? extends Annotation> annotationType) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            return method.getMethodAnnotation(annotationType) != null
                    || method.getBeanType().getDeclaredAnnotation(annotationType) != null;
        }
        return false;
    }

    private void setErrorResponse(HttpServletResponse response, CommonResultCode code) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println(JSONUtil.parse(Result.error(ResultCode.builder()
                .code(code.getCode())
                .zhCn(code.getZhCn())
                .enUs(code.getEnUs())
                .build())).toString());
    }
}
