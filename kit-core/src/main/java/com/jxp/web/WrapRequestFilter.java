package com.jxp.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 在接口调用链中，request的请求流只能调用一次，处理之后，如果之后还需要用到请求流获取数据，就会发现数据为空。
 * 继承HttpServletRequestWrapper，将请求中的流copy一份，复写getInputStream和getReader方法供外部使用。每次调用后的getInputStream
 * 方法都是从复制出来的二进制数组中进行获取，这个二进制数组在对象存在期间一致存在。
 * 使用Filter过滤器，在一开始，替换request为自己定义的可以多次读取流的request
 */

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@WebFilter(urlPatterns = "/*", filterName = "wrapRequestFilter")
@Component
public class WrapRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//        log.info("WrapRequestFilter");
        response.setHeader("Trace-Id", IdUtil.fastUUID());
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        if (isFileUpload(request)) {
            filterChain.doFilter(request, wrappedResponse);
        } else {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        }
        wrappedResponse.copyBodyToResponse();
    }

    private boolean isFileUpload(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().equalsIgnoreCase("multipart/form-data");
    }
}
