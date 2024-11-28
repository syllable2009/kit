package com.jxp.dictionary.service.impl;


import java.util.Dictionary;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jxp.dictionary.domain.DictionaryItem;
import com.jxp.dictionary.service.DictionaryService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-22 14:56
 */

@Slf4j
@Service
public class DictionaryServiceImpl implements DictionaryService {
    @Override
    public Dictionary addDictionaryOne(Dictionary entity) {

        // 流程状态：需求idea 需求设计中 需求评审 需求评审完成 技术方案评审
//                 待开发 开发中 开发完成 开发延期
//                 待测试 测试中 测试完成 测试延期
//                 待验证 灰度中 灰度完成
//                 待发布 发布完成
//                 废弃

        return null;
    }

    @Override
    public List<DictionaryItem> addDictionaryItemList(List<DictionaryItem> entityList) {
        return null;
    }
}
