package com.jxp.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-07-21 11:15
 */
@Slf4j
public class VideoFileFinder {

    private static final List<String> VIDEO_EXTENSIONS = Lists.newArrayList("mp4", "avi", "mov", "mkv", "flv", "wmv",
            "webm", "rmvb", "rm", "mpg", "mpeg", "vob", "ts", "3gp");

    public static void main(String[] args) {
        final Map<String, String> allMovie = getAllMovie("/Users/jiaxiaopeng/Downloads");
        log.info("allMovie:{}", JSONUtil.toJsonStr(allMovie));
    }

    public static Map<String, String> getAllMovie(String path) {
        File root = new File(path);
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("路径不存在或不是文件夹");
        }
        Map<String, String> videoFiles = new HashMap<>();
        traverseDirectory(root, videoFiles);
        return videoFiles;
    }

    private static void traverseDirectory(File dir, Map<String, String> videoFiles) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseDirectory(file, videoFiles);
                } else if (isVideoFile(file)) {
                    log.info("find vedio,name:{},path:{}", file.getName(), file.getPath());
                    videoFiles.put(file.getName(), file.getPath());
                }
            }
        }
    }

    private static boolean isVideoFile(File file) {
        final String ext = FileNameUtil.extName(file);
        return VIDEO_EXTENSIONS.contains(ext.toLowerCase());
    }
}
