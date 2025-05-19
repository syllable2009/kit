package com.jxp.llm.embedding.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.jxp.llm.embedding.dto.SegmentRule;
import com.jxp.llm.embedding.service.SegmentService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 21:33
 */

@Slf4j
@Service
public class SegmentServiceImpl implements SegmentService {

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public List<TextSegment> segment(Document document, SegmentRule rule) {
        // 分段预处理
        contextProcessBeforeSplit("", rule);

        // 字符分割器（CharacterSplitter）块大小5000字符，重叠5字符
        DocumentSplitter splitter = DocumentSplitters.recursive(5000, 5);
        return splitter.split(document);
    }

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:LineLength", "checkstyle:MagicNumber"})
    public static void main(String[] args) {
        String str = "我是424@126.com\n\n,,,,,测试代码块ERROR\t\\t\\t2025-05-19\\n\\n\\nFile not found。。..";

        final SegmentRule rule = SegmentRule.builder()
                .maxLen(5000)
                .minLen(5)
                .overlap(0)
                .removeEmail(true)
                .removeUrl(true)
                .replaceConsecutiveSymbols(true)
                .segmentValue("")
                .segmentType("self")
                .build();

        final String preHandleStr = contextProcessBeforeSplit(str, rule);
        log.info("origin:{}", str);
        log.info("preStr:{}", preHandleStr);


        List<String> textList;
        // 获取分隔符
        final String segmentValue = rule.getSegmentValue();
        if (StrUtil.isNotBlank(segmentValue)) {
            // 默认分段方式
            String regexp = String.join("|", Lists.newArrayList("\\n", "\\.", "。"));
            textList = Arrays.asList(preHandleStr.split("(" + regexp + ")"));
            log.info("regexp:{}", JSONUtil.toJsonStr(textList));
        } else {
            // langchain4j分段
            Document document =
                    Document.from(preHandleStr);
            DocumentSplitter splitter = DocumentSplitters.recursive(5000, 0);
            final List<TextSegment> split = splitter.split(document);
            textList = Optional.ofNullable(split).orElse(new ArrayList<>()).stream()
                    .map(TextSegment::text)
                    .collect(Collectors.toList());
            log.info("splitter:{}", JSONUtil.toJsonStr(textList));
            textList = merge(textList, rule);
        }
    }

    private static String contextProcessBeforeSplit(String context, SegmentRule rule) {
        // 剔除邮箱
        if (rule.getRemoveEmail()) {
            context = ReUtil.delAll(PatternPool.EMAIL, context);
        } // 剔除URL
        if (rule.getRemoveUrl()) {
            context = ReUtil.delAll(PatternPool.URL_HTTP, context);
        }
        // 替换连续标点符号
        if (rule.getReplaceConsecutiveSymbols()) {
            context = reducePunctuation(context);
        }
        // 替换一些特殊字符
        // context = context.replaceAll("\u0000", ""); // removes NUL chars
        context = replaceConsecutiveInvisibleChars(context);
        return context;
    }

    // 使用正则表达式匹配连续的标点符号，并替换为单个标点符号
    // 仅压缩重复标点（保留单个）
    private static String reducePunctuation(String text) {
        return text.replaceAll("(\\p{Punct})\\1+", "$1");
    }

    // 使用正则表达式匹配连续的不可见符号，并替换为单个特定符号
    // \\p{C} 是Unicode属性匹配，表示所有控制字符（包括换行符、制表符等不可见字符）
    private static String replaceConsecutiveInvisibleChars(String text) {
        return text.replaceAll("(\\p{C})\\1+", "$1");
    }

    private static List<String> merge(List<String> segments, SegmentRule rule) {
        if (CollUtil.isEmpty(segments)) {
            return new ArrayList<>();
        }
        int min = rule.getMinLen();
        int max = rule.getMaxLen();
        int overlap = rule.getOverlap();
        List<String> rs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String seg : segments) {
            if (sb.length() < min) {
                sb.append(seg);
            } else {
                int sz = sb.length();
                String segOne = sb.toString();
                if (sz <= max) {
                    rs.add(segOne);
                    int over = sz - overlap;
                    String left = StrUtil.subSuf(sb.toString(), over);
                    sb = new StringBuilder(left);
                } else {
                    rs.add(segOne.substring(0, max));
                    int over = max - overlap;
                    String left = StrUtil.subSuf(sb.toString(), over);
                    sb = new StringBuilder(left);
                }
                sb.append(seg);
            }
        }
        if (sb.length() > 0) {
            rs.addAll(splitWithOverlap(sb.toString(), max, overlap));
        }
        return rs;
    }

    private static List<String> splitWithOverlap(String text, int len, int overlap) {
        List<String> rs = new ArrayList<>();
        while (text.length() > len) {
            int sz = text.length();
            rs.add(text.substring(0, len));
            int over = Math.min(len, sz - overlap);
            text = (text.substring(over));
        }
        if (!text.isEmpty()) {
            rs.add(text);
        }
        return rs;
    }
}
