package com.jxp.component.ai.service.impl;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.RequestHandler;
import com.jxp.component.ai.handler.impl.AuthenticationHandler;
import com.jxp.component.ai.handler.impl.ExpiredSessionHandle;
import com.jxp.component.ai.handler.impl.LoggingHandler;
import com.jxp.component.ai.handler.impl.ManualSessionHandle;
import com.jxp.component.ai.handler.impl.NoSessionHandle;
import com.jxp.component.ai.handler.impl.PermissionHandler;
import com.jxp.component.ai.handler.impl.RobotSessionHandle;
import com.jxp.component.ai.service.AiControllerService;
import com.jxp.component.ai.strategy.DiscountStrategy;
import com.jxp.component.ai.strategy.impl.AdminUserDiscountStrategy;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.SessionCacheDTO;
import com.jxp.component.customer.service.AiService;
import com.jxp.component.customer.service.ConfigService;
import com.jxp.component.customer.service.ManualService;
import com.jxp.component.customer.service.MessageService;
import com.jxp.component.customer.service.SessionService;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 责任链模式、过滤器模式和策略模式的结合应用能够有效地解耦请求的发送者和接收者，实现灵活的请求处理流程，并根据不同的情况提供不同的处理策略。
 * 这种组合在Web应用程序、购物网站等场景中都非常有用。通过合理地使用这三种模式，我们可以实现更加灵活、可维护和可扩展的代码结构。
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:44
 */
@Slf4j
@Service
public class AiControllerServiceImpl implements AiControllerService {

    @Resource
    private SessionService sessionService;
    @Resource
    private ConfigService configService;
    @Resource
    private AiService aiService;
    @Resource
    private MessageService messageService;
    @Resource
    private ManualService manualService;

    @Override
    public String chat(Request request) {
        // 创建处理器的链
        RequestFilterChain filterChain = new RequestFilterChain();
        // 根据配置创建处理器，注意顺序
        filterChain.addHandler(new AuthenticationHandler());
        filterChain.addHandler(new LoggingHandler());
        filterChain.addHandler(new PermissionHandler());

        // 处理请求
        System.out.println("Processing request 1:");
        filterChain.doFilter(request);
        System.out.println("\nProcessing request 2:");
        // 选择策略
        applyDiscount(request, new AdminUserDiscountStrategy());
        return "success";
    }

    private static void applyDiscount(Request request, DiscountStrategy discountStrategy) {
        double discountedAmount = discountStrategy.applyDiscount(request.getAmount());
        System.out.println("Discounted amount for user " + request.getUsername() + ": " + discountedAmount);
    }

    public static void main(String[] args) {
        final AiControllerServiceImpl aiControllerService = new AiControllerServiceImpl();
        aiControllerService.chat(Request.builder()
                .username("admin")
                .amount(100)
                .build());
    }

    private String chat2(Request request) {
        RequestFilterChain filterChain = new RequestFilterChain();
        // 消息去重过滤器
        filterChain.addHandler(null);
        // 事件流程控制判断
        final String eventType = request.getEventType();
        // 根据不同的eventType添加不同的处理器
        switch (eventType) {
            case "enterApp":
                // 进入app
                filterChain.addHandler(null);
                break;
            case "groupChat":
                // 群消息，需要引导到消息号
                break;
            case "customerServiceGroup":
                // 客服群,此时消息需要传给用户
                break;
            case "groupRemoveUser":
            case "groupAddUser":
                // 客服群人员变化
                break;
            case "userMessage":
                // 用户单独向消息号发送信息，判断添加会话管理处理器，消息处理器
                filterChain.addHandler(new RobotSessionHandle());
                break;
            default:
                // 其他类型，打个日志
                break;
        }
        log.info("filterChain:{}",
                filterChain.getHandlers().stream()
                        .map(RequestHandler::getName
                        ).collect(Collectors.toList()));
        filterChain.doFilter(request);
        return "";
    }

    private void handlerUserMessage(Request request, RequestFilterChain filterChain) {
        final SessionCacheDTO session = sessionService.getByUserId(request.getUserId());
        request.setSession(session);
        if (null == session) {
            // 添加空会话处理器
            filterChain.addHandler(new NoSessionHandle());
        } else {
            // 会话检测
            final AppSessionConfigDTO appConfig =
                    configService.getAppSessionConfig(request.getAppId());
            // 会话是否过期，是否有效
            final long lastStimestamp = session.getLastStimestamp();
            final long currentStimestamp = System.currentTimeMillis();
            // 会话失效过期
            if (currentStimestamp - lastStimestamp > appConfig.getSessionInterval()) {
                // 添加失效会话处理器
                filterChain.addHandler(new ExpiredSessionHandle());
            } else {
                if (StrUtil.equals("robot", session.getType())) {
                    // 添加机器人会话处理器
                    filterChain.addHandler(new RobotSessionHandle());
                } else if (StrUtil.equals("manual", session.getType())) {
                    // 添加人工会话处理器
                    filterChain.addHandler(new ManualSessionHandle());
                } else {
                    // 添加异常会话处理器
                    filterChain.addHandler(null);
                }
            }
        }
    }
}
