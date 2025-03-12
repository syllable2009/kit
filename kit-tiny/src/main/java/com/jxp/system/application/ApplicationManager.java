package com.jxp.system.application;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashBasedTable;
import com.jxp.system.annotation.Action;
import com.jxp.system.annotation.Argument;
import com.jxp.system.domain.Command;
import com.jxp.system.domain.Mode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationManager=应用上下文，RequestContext=请求上下文
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
    private static Props props = null;
    public static final HashBasedTable<String, String, Command> COMMAND_MAP = HashBasedTable.create();

    public static void main(String[] args) {
        log.info("ApplicationManager main start,args:{}", JSONUtil.toJsonStr(args));
        // 准备请求上下文
        final RequestContext requestContext = new RequestContext();

        // 参数解析
        requestContext.parserParams(args);
        // 配置路径
        ApplicationManager.settings = "application.properties";
        // 初始化
        ApplicationManager.init();
        // 安装扫描解析应用app
        final Object importObj = requestContext.getRequestOptions().get("import");
        if (null != importObj) {
            StrUtil.split(importObj.toString(), ";")
                    .stream()
                    .filter(StrUtil::isNotBlank)
                    .forEach(ApplicationManager::install);
        }
        // 命令执行
        ApplicationManager.execCommand(requestContext);
    }

    private static void init() {
        if (initialized) {
            log.info("ApplicationManager init return,initialized is true");
            return;
        }

        synchronized (ApplicationManager.class) {
            if (initialized) {
                return;
            }
            log.info("ApplicationManager init");
            if (StrUtil.isNotBlank(settings)) {
                log.info("ApplicationManager init setting");
                // 加载配置
                props = new Props(settings);
                // 初始化安装
                final String improtStr = props.getStr("default.import.applications");
                StrUtil.split(improtStr, ";")
                        .stream()
                        .filter(StrUtil::isNotBlank)
                        .forEach(ApplicationManager::install);
            }
            initialized = true;
        }
    }

    private static void install(String appClassName) {
        if (null == appClassName || 0 == appClassName.length()) {
            return;
        }
        if (applications.containsKey(appClassName)) {
            log.info("ApplicationManager install return,appClassName:{} has install", appClassName);
            return;
        }
        try {
            Object app = Class.forName(appClassName).getDeclaredConstructor().newInstance();
            processActionAnnotations(app);
            applications.put(appClassName, app);
            log.info("ApplicationManager install success,appClassName:{}", appClassName);
        } catch (Exception e) {
            log.error("ApplicationManager install app error,appClassName:{},", appClassName, e);
        }
    }

    // 执行
    private static Object call(final String path, final RequestContext context, final Mode mode) {
        final Command command = COMMAND_MAP.get(path, "get");
        if (null == command) {
            log.info("call return,command not found,path:{},context:{},mode:{}", path, context, mode);
            return null;
        }
        // ordinal()返回枚举常量的序号，从0开始
        if (command.getMode().ordinal() < mode.ordinal()) {
            log.info("call return,command mode not support,path:{},context:{},mode:{}", path, context, mode);
            return null;
        }

        final MethodHandle methodHandle = command.getMethodHandle();
        if (null == methodHandle) {
            log.info("call return,methodHandle is null,path:{},context:{},mode:{}", path, context, mode);
            return null;
        }
        final Object[] executeArguments = getExecuteArguments(command, context);
        // 如何执行
        try {
            final Object result = methodHandle.invokeWithArguments(executeArguments);
            log.info("call success,resut:{},path:{},context:{},mode:{}", result, path, context, mode);
            if (command.getReturnType().isAssignableFrom(Void.TYPE)) {
                return null;
            }
            return result;
        } catch (Throwable e) {
            log.error("call error,path:{},context:{},mode:{}", path, context, mode);
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
//        Action annotation = app.getClass().getAnnotation(Action.class);
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
                command.setOptions(actionAnnotation.options());
                COMMAND_MAP.put(path, "get", command);
            }
        }
    }

    private static Object[] getExecuteArguments(Command command, RequestContext context) {

        final Argument[] options = command.getOptions();
        final Class<?>[] paraTypes = command.getParameterTypes();
        if (ObjUtil.hasNull(options, paraTypes) || options.length != paraTypes.length) {
            log.info("getExecuteArguments return,,command:{},arguments not match parameterTypes", command);
            return new Object[]{command.getApp()};
        }

        Object[] params = new Object[paraTypes.length + 1];
        // 第一个参数为对象实例
        params[0] = command.getApp();
        final Map<String, Object> requestOptions = context.getRequestOptions();
        Argument argument = null;
        for (int i = 0; i < paraTypes.length; i++) {
            Class<?> targetType = paraTypes[i];
            argument = options[i];
            Object arg = requestOptions.get(argument.key()); // 从请求中获取
            if (null == arg) {
                if (!argument.required()) {
                    arg = argument.value();
                }
            }
            params[i + 1] = convertArgument(arg, targetType);
        }
        return params;
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    private static Object convertArgument(Object arg, Class<?> targetType) {
        if (arg == null) {
            // Return null if the input argument is null.
            return null;
        }
        // Convert argument to string for parsing.
        String _arg = String.valueOf(arg);
        try {
            // Handle Date type conversion.
            if (Date.class.isAssignableFrom(targetType)) {
                return parseDate(_arg);
            } else if (targetType.isPrimitive() || Number.class.isAssignableFrom(targetType)) {
                // Handle primitive and wrapper number types.
                return parsePrimitive(_arg, targetType);
            } else if (Boolean.TYPE.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
                // Handle Boolean type conversion.
                return Boolean.valueOf(_arg);
            } else if (targetType.isEnum()) {
                return Enum.valueOf((Class<Enum>) targetType, _arg);
            } else {
                // Default case: Return the argument as-is.
                return arg;
            }
        } catch (Exception e) {
            // Wrap and rethrow any conversion errors with additional context.
            throw new RuntimeException("Error converting argument: " + _arg, e);
        }
    }

    private static Date parseDate(String dateString) throws ParseException {
        String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
        String extendedDateFormat = "yyyy-MM-dd HH:mm:ss z";

        // Choose the appropriate format based on the input length.
        SimpleDateFormat formatter = dateString.length() < extendedDateFormat.length()
                ? new SimpleDateFormat(defaultDateFormat)
                : new SimpleDateFormat(extendedDateFormat);

        return formatter.parse(dateString);
    }

    /**
     * Parses a string into a primitive or wrapper type object.
     *
     * @param arg        The string representation of the argument.
     * @param targetType The target type to convert to.
     * @return The converted primitive or wrapper type object.
     */
    private static Object parsePrimitive(String arg, Class<?> targetType) {
        // Switch block for both null handling and type conversion
        switch (targetType.getName()) {
            case "int":
            case "java.lang.Integer":
                return (arg == null) ? 0 : Integer.parseInt(arg);
            case "long":
            case "java.lang.Long":
                return (arg == null) ? 0L : Long.parseLong(arg);
            case "float":
            case "java.lang.Float":
                return (arg == null) ? 0.0f : Float.parseFloat(arg);
            case "double":
            case "java.lang.Double":
                return (arg == null) ? 0.0d : Double.parseDouble(arg);
            case "boolean":
            case "java.lang.Boolean":
                return Boolean.parseBoolean(arg);
            case "char":
            case "java.lang.Character":
                return (arg == null || arg.isEmpty()) ? '\u0000' : arg.charAt(0);
            case "short":
            case "java.lang.Short":
                return (arg == null) ? (short) 0 : Short.parseShort(arg);
            case "byte":
            case "java.lang.Byte":
                return (arg == null) ? (byte) 0 : Byte.parseByte(arg);
            default:
                throw new IllegalArgumentException("Unsupported type: " + targetType.getName());
        }
    }
}
