package com.jxp.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
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
@Slf4j
@Order(FilterOrder.TWO)
@WebFilter(urlPatterns = "/*", filterName = "accessLogFilter")
@Component
public class AccessLogFilter extends OncePerRequestFilter {

    private static final int maxLogLength = 1024;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (RequestMethod.HEAD.name().equals(request.getMethod())) {
                return;
            }
            log.info("AccessLogFilter");
            // request log
            String requestBody = getRequestBody(request);
            String requestBodyLog = StrUtil.subWithLength(requestBody, 0, maxLogLength);
            // response log
            ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
            final String str = StrUtil.str(wrappedResponse.getContentAsByteArray(), "UTF-8");
            String responseBodyLog = StrUtil.subWithLength(str, 0,
                    maxLogLength);
            // ext log
            // 获取上下文对象,里面包含一些额外信息，结合
            String userId = RequestContext.getUserId(); // 可从上下文中获取requestContextDto.getRequestTimestamp()
            long cost = System.currentTimeMillis() - RequestContext.getRequestTimestamp();
            log.info("method={} path={} httpStatus={} cost={} userId={} traceId={} "
                            + "language={} query={} request={} response={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), cost,
                    userId, response.getHeader("Trace-Id"),
                    request.getLocale().getLanguage(), request.getQueryString(), requestBodyLog, responseBodyLog
            );
            RequestContext.clear();
        }
    }
}
