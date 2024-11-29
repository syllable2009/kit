package com.jxp.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.jxp.tool.JacksonUtils;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-29 11:13
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
//@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    int maxLogLength = 1024;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable
    Exception ex)
            throws Exception {
//        log.info("AccessLogInterceptor");
        if (RequestMethod.HEAD.name().equals(request.getMethod()) || !isJsonResponse(response)) {
            return;
        }
        // request log
        String requestBody = getRequestBody(request);
        String requestBodyLog = StrUtil.subWithLength(requestBody, 0, maxLogLength);
        // response log
        ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
        JsonNode responseJsonNode = JacksonUtils.parseObj(wrappedResponse.getContentAsByteArray());
        String responseBodyLog = StrUtil.subWithLength(JacksonUtils.toJsonStr(responseJsonNode),0,
                maxLogLength);
        // ext log
        // 获取上下文对象,里面包含一些额外信息
        String userId = RequestContext.getUserId(); // 可从上下文中获取requestContextDto.getRequestTimestamp()
        long cost = System.currentTimeMillis() - RequestContext.getRequestTimestamp();
        log.info("method={} path={} httpStatus={} cost={} userId={} traceId={} "
                        + "language={} query={} request={} response={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(), cost,
                userId, response.getHeader("Trace-Id"),
                request.getLocale().getLanguage(), request.getQueryString(), requestBodyLog, responseBodyLog
        );
    }

    @SneakyThrows
    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
            String requestBody = getRequestBody(wrappedRequest);
            if (StrUtil.isBlank(requestBody)) {
                return requestBody;
            }
            if (isJsonRequest(request)) {
                JsonNode jsonNode = JacksonUtils.parseObj(requestBody);
                return JacksonUtils.toJsonStr(jsonNode);
            } else {
                return requestBody;
            }
        }
        return null;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, 0, buf.length, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
        return null;
    }

    private boolean isJsonResponse(HttpServletResponse response) {
        return response.getContentType() != null
                && response.getContentType().toLowerCase().startsWith("application/json");
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        return "application/json".equalsIgnoreCase(request.getContentType());
    }
}
