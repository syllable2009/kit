package com.jxp.web;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Sets;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2023-06-28 11:40
 * order由小到大
 * 和IdentifyInterceptor效果一样，二选一
 * IdentifyFilter的实例化在configration里面做，可以排除掉部分url
 */
//@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class IdentifyFilter extends OncePerRequestFilter {

    public static final String DEBUG_TEST_USER_KEY = "testUser";

    public static final String ANONYMOUS_USER_KEY = "I-Token";

    public static final String T_TOKEN = "T-Token";

    public static final String HEADER_ACCOUNT_ID = "Account-Id";

    public static final Set<String> K_UIMS_COOKIES =
            Sets.newHashSet(T_TOKEN, HEADER_ACCOUNT_ID);

    public static final long TOKEN_EXPIRE_TIME = Duration.ofDays(15).getSeconds();

    @Resource
    private Environment environment;

    private static String getLanguage(HttpServletRequest request) {
        String language = null; // 安卓：zh或en
        Locale locale = LocaleContextHolder.getLocale();
        if (null != locale) {
            language = locale.getLanguage();
        }
        if (StrUtil.isBlank(language)) {
            final String header = request.getHeader("language");
            if (StrUtil.isNotBlank(header)) {
                language = header;
            }
        }
        if (StrUtil.isBlank(language)) {
            language = "zh";
        }
        return language;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("IdentifyFilter start");
        // 优先级 testUserId > 登录 > 匿名
        // 先要判断环境
        final String[] activeProfiles = environment.getActiveProfiles();
        if (ArrayUtil.containsAny(activeProfiles, "test", "dev", "local")) {
            Context context = buildDebugContext(request);
            if (null != context) {
                RequestContext.setRequestContext(context);
                filterChain.doFilter(request, response);
                RequestContext.clear();
                log.info("IdentifyFilter end");
                return;
            }
        }
        // 构建登录用户，这里缓存信息
        Context context = buildUserContext(request);
        if (null != context) {
            RequestContext.setRequestContext(context);
            filterChain.doFilter(request, response);
            RequestContext.clear();
            log.info("IdentifyFilter end");
            return;
        }

        // 构建匿名用户：匿名用户行为跟踪
        context = buildAnonymousContext(request);
        if (null != context) {
            RequestContext.setRequestContext(context);
            filterChain.doFilter(request, response);
            RequestContext.clear();
            log.info("IdentifyFilter end");
            return;
        }

        // 构建空用户
        context = buildUnkownContext(request);
        RequestContext.setRequestContext(context);
        filterChain.doFilter(request, response);
        RequestContext.clear();
        log.info("IdentifyFilter end");
    }

    public Context buildUserContext(HttpServletRequest request) {
        String token = request.getHeader(T_TOKEN);
        String accountId = request.getHeader(HEADER_ACCOUNT_ID);
        if (StrUtil.isBlank(accountId) || StrUtil.isBlank(token)) {
            final Map<String, Cookie> stringCookieMap = buildCookies(request);
            if (StrUtil.isBlank(token)) {
                final Cookie cookie = stringCookieMap.get(T_TOKEN);
                if (null != cookie) {
                    token = cookie.getValue();
                }
            }

            if (StrUtil.isBlank(accountId)) {
                final Cookie cookie = stringCookieMap.get(HEADER_ACCOUNT_ID);
                if (null != cookie) {
                    accountId = cookie.getValue();
                }
            }
        }
        if (StrUtil.isBlank(accountId) || StrUtil.isBlank(token)) {
            return null;
        }
        // rpc校验换取token并缓存
        return Context.builder()
                .userId("admin")
                .anonymous(false)
                .requestTimestamp(System.currentTimeMillis())
                .language(getLanguage(request))
                .build();
    }

    public static Map<String, Cookie> buildCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return new HashMap<>();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> K_UIMS_COOKIES.contains(cookie.getName()))
                .collect(Collectors.toMap(e -> e.getName(), Function.identity()));
    }


    private static Context buildDebugContext(HttpServletRequest request) {
        String testUserId = request.getParameter(DEBUG_TEST_USER_KEY);
        if (StrUtil.isBlank(testUserId)) {
            return null;
        }
        return Context.builder()
                .userId(testUserId)
                .anonymous(false)
                .requestTimestamp(System.currentTimeMillis())
                .language(getLanguage(request))
                .build();
    }

    private Context buildAnonymousContext(HttpServletRequest request) {
        String anonymousToken = getAnonymousToken(request);
        if (StrUtil.isBlank(anonymousToken)) {
            return null;
        }
        return Context.builder()
                .userId(anonymousToken)
                .anonymous(true)
                .language(getLanguage(request))
                .requestTimestamp(System.currentTimeMillis())
                .build();
    }

    private Context buildUnkownContext(HttpServletRequest request) {
        return Context.builder()
                .userId(null)
                .anonymous(null)
                .language(getLanguage(request))
                .requestTimestamp(System.currentTimeMillis())
                .build();
    }

    private static String getAnonymousToken(HttpServletRequest request) {
        Cookie cookie = ServletUtil.getCookie(request, ANONYMOUS_USER_KEY);
        if (cookie != null) {
            return cookie.getValue();
        }
        return request.getHeader(ANONYMOUS_USER_KEY);
    }
}
