package com.jxp.system.application;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashBasedTable;
import com.jxp.system.annotation.Action;
import com.jxp.system.domain.Command;
import com.jxp.system.domain.Mode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jiaxiaopeng
 * Created on 2025-03-12 11:38
 */

@Slf4j
@SuppressWarnings("checkstyle:ConstantName")
public class ApplicationManager {
    public static final String version = "1.0.0";
    private static final ConcurrentHashMap<String, Object> applications = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;
    private static String settings = null;
    public static final HashBasedTable<String, String, Command> COMMAND_MAP = HashBasedTable.create();

    public static void main(String[] args) {
        log.info("main start:{}", JSONUtil.toJsonStr(args));
        // 准备请求上下文
        final RequestContext requestContext = new RequestContext();
        // 参数解析
        requestContext.parserParams(args);
        requestContext.getRequsetCommand().add("praise");
        // 获取通用的配置

        // 初始化
        ApplicationManager.init();
        // 安装扫描解析应用app
        ApplicationManager.install("com.jxp.demo.Example");
        // 命令执行
        ApplicationManager.execCommand(requestContext);
    }

    public static void loadConfig(String config) {

    }

    private static void init() {
        if (initialized) {
            return;
        }

        synchronized (ApplicationManager.class) {
            if (initialized) {
                return;
            }
            // —D参数设置

            if (null != settings) {
                log.info("setting init");
                // 加载配置
                // 初始化安装
            }
            initialized = true;
        }
    }

    private static void install(String appClassName) {
        if (null == appClassName || 0 == appClassName.length()) {
            return;
        }
        if (applications.contains(appClassName)) {
            log.info("app:{} has install", appClassName);
            return;
        }
        try {
            Object app = Class.forName(appClassName).getDeclaredConstructor().newInstance();
            processActionAnnotations(app);
        } catch (Exception e) {
            log.info("install app error,appClassName:{},", appClassName, e);
        }
    }

    // 执行
    private static void call(final String path, final RequestContext context, final Mode mode) {
        final Command command = COMMAND_MAP.get(path, "get");
        if (null == command) {
            log.info("call return,command not found,path:{},context:{},mode:{}", path, context, mode);
            return;
        }
        final MethodHandle methodHandle = command.getMethodHandle();
        if (null == methodHandle) {
            log.info("call return,methodHandle is null,path:{},context:{},mode:{}", path, context, mode);
            return;
        }
//        getExecuteArguments(command.getApp(), , new HashMap<>());
        // 如何执行
        try {
            final Object result = methodHandle.invokeWithArguments(new Object[]{command.getApp()});
            log.info("call resut:{},path:{},context:{},mode:{}", result, path, context, mode);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void execCommand(RequestContext requestContext) {
        final List<String> requsetCommand = requestContext.getRequsetCommand();
        if (CollUtil.isNotEmpty(requsetCommand)) {
            requsetCommand.forEach(e -> ApplicationManager.call(e, requestContext, Mode.All));
        }
    }

    private static void processActionAnnotations(Object app) {
        Action annotation = app.getClass().getAnnotation(Action.class);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        String path = null;
        for (java.lang.reflect.Method method : app.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null) {
                path = actionAnnotation.value();
                MethodHandle methodHandle = null;
                try {
                    methodHandle = lookup.findVirtual(app.getClass(), method.getName(),
                            MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
                } catch (Exception e) {
                    log.info("processActionAnnotations error,app:{},", app, e);
                }
                if (null == methodHandle) {
                    continue;
                }
                Command command = new Command();
                command.setApp(app);
                command.setMethod(actionAnnotation.value());
                command.setMethodHandle(methodHandle);
                command.setParameterTypes(method.getParameterTypes());
                command.setReturnType(method.getReturnType());
                command.setMode(actionAnnotation.mode());
                COMMAND_MAP.put(path, "get", command);
            }
        }
    }
}
