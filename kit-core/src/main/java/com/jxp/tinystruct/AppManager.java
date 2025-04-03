package com.jxp.tinystruct;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.jxp.tinystruct.annotation.Action;
import com.jxp.tinystruct.annotation.Param;
import com.jxp.tinystruct.domain.Command;
import com.jxp.tinystruct.domain.CommandParam;
import com.jxp.tinystruct.service.Configuration;
import com.jxp.tinystruct.service.Settings;
import com.jxp.web.Context;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 10:45
 */

@Slf4j
public class AppManager {

    public static final HashBasedTable<String, String, Command> ACTION_MAP = HashBasedTable.create();
    public static final String VERSION = "1.5.5";
    private static final ConcurrentHashMap<String, Object> applications = new ConcurrentHashMap<>();
    private static Configuration<String> settings;
    private static volatile boolean initialized = false;


    private AppManager() {
    }

    public static void main(String[] args) throws Throwable {

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Example example = new Example();
//        final Object o = Class.forName(Example.class.getName()).getDeclaredConstructor().newInstance();
//        log.info("className:{},o:{}", Example.class.getName(), o);
        for (java.lang.reflect.Method method : Example.class.getDeclaredMethods()) {
            final Action annotation = method.getAnnotation(Action.class);
            if (null == annotation) {
                continue;
            }
            final String methodName = method.getName();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?> returnType = method.getReturnType();
//            final Parameter[] parameters = method.getParameters();
//            if (parameters.length > 0) {
//                System.out.println("");
//            }
//            System.out.println("name:" + methodName + ":"
//                    + JSONUtil.toJsonStr(Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.joining(","))));
            MethodHandle handle = lookup.findVirtual(Example.class, methodName,
                    MethodType.methodType(returnType, parameterTypes));
            ACTION_MAP.put(methodName, annotation.method(), Command.builder()
                    .instance(example)
                    .methodHandle(handle)
                    .parameterTypes(parameterTypes)
                    .returnType(returnType)
                    .params(getCommandArguments(annotation))
                    .build());
        }
        String path = "praise";
        final Command command = ACTION_MAP.get(path, "");
        if (null == command) {
            log.error("404 No matching function found for path [" + path + "]. Ensure the path is"
                    + " correct and the function is public.");
            return;
        }
        final MethodHandle methodHandle = command.getMethodHandle();
        Map<String, String> requstMap = new HashMap<>();
        final Object[] executeParams = getExecuteParams(command, requstMap);
        final Object result = methodHandle.invokeWithArguments(executeParams);
        if (command.getReturnType().isAssignableFrom(Void.TYPE)) {
            return;
        }
        log.info("result:{}", result);

        List<String> commands = new ArrayList<>();
        Map<String, String> requestMap = new ConcurrentHashMap<>();
        // 参数解析与校验
        analyzeParameters(args);
        // 初始化配置，不同的app启动类从不同的地方加载
        Settings config = new Settings("application.properties");
        // 加载配置，参数配置替换默认配置吗？？？
        AppManager.loadConfig(config, requestMap);
        // 初始化工厂
        AppManager.init();
        final String importClassName = requestMap.get("import");
//        AppManager.installApp(AppRun.class.getName());

        commands.parallelStream()
                .forEach(e -> {
                    try {
                        AppManager.call(e, null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    public static void loadConfig(Configuration<String> config, Map<String, String> attrs) {
        // 参数传值覆盖
//        config.set();
        settings = config;
    }

    public static void init() throws Exception {
        if (initialized) {
            return;
        }
        if (null == settings) {
            System.out.println("config is null,can not init");
            return;
        }
        synchronized (AppManager.class) {
            if (initialized) {
                return;
            }
            // 设置命令
            // 装载配置类
            final String initClass = settings.get("default.import.applications");
            System.out.println("default.import.applications:" + initClass);
            if (null != initClass && initClass.length() > 0) {
                // 逗号分割-实例化-install
                Arrays.stream(initClass.split(";")).forEach(e -> installApp(e));
            }
            initialized = true;
        }
    }

    public static void installApp(String appClassName) {

        if (null == appClassName || 0 == appClassName.length()) {
            return;
        }
        if (applications.contains(appClassName)) {
            System.out.println("app has install");
            return;
        }
        try {
            Object app = Class.forName(appClassName).getDeclaredConstructor().newInstance();
            AnnotationProcessor annotationProcessor = new AnnotationProcessor(app);
            annotationProcessor.processActionAnnotations();
            final Map<String, CommandLine> commandLines = app.getCommandLines();
            for (Map.Entry<String, CommandLine> entry : commandLines.entrySet()) {
                System.out.println("action-command:" + entry.getKey() + ":" + entry.getValue().getOptions());
            }
        } catch (Exception e) {
            System.out.println("install app error," + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object call(final String path, final Context requestContext,
            final Action.Mode mode) throws RuntimeException {

        return null;
    }

    // 参数解析：-D为环境变量，格式-Dx=y
    // --为K=V，格式为 --k=v 或者 --k v --k -D
    // -a，格式为--a=true
    // 其余为命令
    private static void analyzeParameters(String[] args) {
        System.out.println("origin-args:" + Arrays.stream(args).collect(Collectors.joining("|")));
        int index = 0;
        String current;
        while (index < args.length) {
            current = args[index];
            if (null == current || current.length() == 0) {
                index++;
            } else if (current.startsWith("-")) {
                index = processLongOption(null, args, index);
            } else if (current.startsWith("-D")) {
                // 处理环境变量
                index = processSystemProperty(current, index);
            } else {
                commands.add(current);
                index++;
            }
        }
        System.out.println("command-args:" + commands.stream().collect(Collectors.joining("|")));
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            System.out.println("attr-args:" + entry.getKey() + ":" + entry.getValue());
        }
    }

    /**
     * 获取参数的方式一种是在@Action上指定，然后在数据中通过上下文指定
     * @Action(value = "", description = "A command line tool for tinystruct framework",
     *         options = {
     *                 @Argument(key = "import", description = "Import application"),
     *                 @Argument(key = "help", description = "Print help information")
     *         }, mode = org.tinystruct.application.Action.Mode.CLI)
     * 另一种方式是在参数上加param然后解析顺序，需要每个参数都指定
     * @param command
     * @param requstMap
     * @return
     */
    private static Object[] getExecuteParams(Command command, Map<String, String> requstMap) {
        final Class<?>[] parameterTypes = command.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length + 1];
        // 第一个参数为对象实例
        arguments[0] = command.getInstance();
        if (parameterTypes.length == 0) {
            return arguments;
        }
        String value = null;
        final List<CommandParam<String, Object>> params = command.getParams();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> targetType = parameterTypes[i];
            CommandParam<String, Object> parm = params.get(i);
            value = requstMap.get(parm.getKey());
            if (parm.isOptional()) {
                arguments[i + 1] = convertArgument(null != value ? value : parm.getValue(), targetType);
            } else {
                arguments[i + 1] = convertArgument(value, targetType);
            }
        }
        return arguments;
    }

    private static List<CommandParam<String, Object>> getCommandArguments(Action actionAnnotation) {
        Param[] argumentAnnotations = actionAnnotation.params();
        return Arrays.stream(argumentAnnotations)
                .map(e -> CommandParam.<String, Object>builder()
                        .key(e.value())
                        .description(e.description())
                        .optional(e.optional())
                        .value(e.init())
                        .build())
                .collect(Collectors.toList());
    }

    private static Object convertArgument(Object arg, Class<?> targetType) {
        if (arg == null) {
            return null; // Return null if the input argument is null.
        }

        String _arg = String.valueOf(arg); // Convert argument to string for parsing.
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

    /* 核心处理逻辑分解 */
    private static int processLongOption(Context context, String[] args, int index) {

        String option = null;
        if (args[index].startsWith("--")) {
            option = args[index].substring(2);
        } else {
            option = args[index].substring(1);
        }

        String[] parts = option.split("=", 2);

        // 处理 --key=value 形式
        if (parts.length == 2) {
            attrs.put(parts[0], parts[1]);
//            updateContextAttribute(context, parts[0], parts[1]);
            return index + 1;
        }

        // 处理 --key value 形式
        if (index + 1 < args.length && !isOption(args[index + 1])) {
            attrs.put(parts[0], args[index + 1]);
//            updateContextAttribute(context, option, args[index + 1].trim());
            return index + 2;
        }

        // 无值参数处理
//        context.setAttribute(option, Boolean.TRUE);
        attrs.put(option, "true");
        return index + 1;
    }

    private static int processSystemProperty(String arg, int index) {
        String[] parts = arg.substring(2).split("=", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid system property: " + arg);
        }
        System.setProperty(parts[0], parts[1]);
        return index++;
    }

    /* 辅助方法 */
    private static boolean isPlainCommand(String arg) {
        return !arg.startsWith("--") && !arg.startsWith("-") && !arg.contains("=");
    }

    private static boolean isOption(String arg) {
        return arg.startsWith("--") || arg.startsWith("-");
    }
}
