package com.jxp.dictionary.service;

import java.util.Dictionary;
import java.util.List;

import com.jxp.dictionary.domain.DictionaryItem;


/**
 * 流程：
 * 创建一个Dictionary，例如性别
 * 在创建DictionaryItem，例如男和女
 * DictionaryRelation做关联
 *
 * @author jiaxiaopeng
 * Created on 2024-11-22 14:37
 */
public interface DictionaryService {

    // 添加一个数据字典
    Dictionary addDictionaryOne(Dictionary entity);

    // 添加数据字典的item
    List<DictionaryItem> addDictionaryItemList(List<DictionaryItem> entityList);
}
