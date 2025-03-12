package com.jxp.system.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求上下文
 * @author jiaxiaopeng
 * Created on 2025-03-12 14:50
 */

@NoArgsConstructor
@Data
@Slf4j
public class RequestContext {

    private List<String> requsetCommand = new ArrayList<>();

    private Map<String, Object> requestOptions = new ConcurrentHashMap<>();

    public void parserParams(String[] args) {
        int index = 0;
        String current;
        while (index < args.length) {
            current = args[index];
            if (null == current || current.length() == 0) {
                index++;
            } else if (current.startsWith("-D")) {
                // 处理环境变量
                index = processSystemProperty(current, index);
            } else if (current.startsWith("-")) {
                index = processLongOption(args, index);
            } else {
                requsetCommand.add(current);
                index++;
            }
        }
    }

    /* 核心处理逻辑分解 */
    private int processLongOption(String[] args, int index) {

        String option = null;
        if (args[index].startsWith("--")) {
            option = args[index].substring(2);
        } else {
            option = args[index].substring(1);
        }

        String[] parts = option.split("=", 2);

        // 处理 --key=value 形式
        if (parts.length == 2) {
            requestOptions.put(parts[0], parts[1]);
            return index + 1;
        }

        // 处理 --key value 形式
        if (index + 1 < args.length && !isOption(args[index + 1])) {
            requestOptions.put(parts[0], args[index + 1]);
            return index + 2;
        }

        // 无值参数处理
        requestOptions.put(option, true);
        return index + 1;
    }

    private static int processSystemProperty(String arg, int index) {
        String[] parts = arg.substring(2).split("=", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid system property: " + arg);
        }
        // 在 JVM 全局范围内生效，通常用于配置运行时环境或传递关键参数
        System.setProperty(parts[0], parts[1]);
        return index + 1;
    }

    /* 辅助方法 */
    private static boolean isPlainCommand(String arg) {
        return !arg.startsWith("--") && !arg.startsWith("-") && !arg.contains("=");
    }

    private static boolean isOption(String arg) {
        return arg.startsWith("--") || arg.startsWith("-");
    }
}
