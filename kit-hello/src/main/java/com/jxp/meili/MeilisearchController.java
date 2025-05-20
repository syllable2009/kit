package com.jxp.meili;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.MatchingStrategy;
import com.meilisearch.sdk.model.Searchable;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TaskInfo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-06 16:28
 */
@RestController
@RequestMapping("/meilisearch")
public class MeilisearchController {

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${meilisearch.index}")
    private String MEILISEARCH_INDEX;

    @Autowired
    private Client searchClient;


    @Operation(summary = "获取索引设置")
    @GetMapping("/getSettings")
    public ResponseEntity getSettings() {
        Settings settings = searchClient.index(MEILISEARCH_INDEX).getSettings();
        return ResponseEntity.ok(settings);
    }

    @Operation(summary = "修改索引设置")
    @GetMapping("/updateSettings")
    public ResponseEntity updateSettings() {
        Settings settings = new Settings();
        settings.setFilterableAttributes(new String[]{"productCategoryName"});
        settings.setSortableAttributes(new String[]{"price"});
        TaskInfo info = searchClient.index(MEILISEARCH_INDEX).updateSettings(settings);
        return ResponseEntity.ok(info);
    }


    @Operation(summary = "创建索引并导入商品数据")
    @GetMapping("/createIndex")
    public ResponseEntity createIndex() {
        ClassPathResource resource = new ClassPathResource("json/products.json");
        String jsonStr = IoUtil.read(resource.getStream(), Charset.forName("UTF-8"));
        Index index = searchClient.index(MEILISEARCH_INDEX);
        TaskInfo info = index.addDocuments(jsonStr, "id");
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "刪除商品索引")
    @GetMapping("/deleteIndex")
    public ResponseEntity deleteIndex() {
        TaskInfo info = searchClient.deleteIndex(MEILISEARCH_INDEX);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "根据关键字分页搜索商品")
    @GetMapping(value = "/search")
    @ResponseBody
    public ResponseEntity search(@RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String productCategoryName,
            @RequestParam(required = false, value = "0->按价格升序；1->按价格降序") Integer order) {
        SearchRequest.SearchRequestBuilder searchBuilder = SearchRequest.builder();
        searchBuilder.attributesToSearchOn(new String[]{"name", "subTitle"}); // 设置检索字段
        searchBuilder.attributesToHighlight(new String[]{"name", "subTitle"}); //设置高亮字段
        searchBuilder.matchingStrategy(MatchingStrategy.ALL);
        searchBuilder.page(pageNum);  // 页码，从1开始
        searchBuilder.hitsPerPage(pageSize); // 每页数量
        if (StrUtil.isNotEmpty(keyword)) {
            searchBuilder.q(keyword);  // 搜索的关键字
        }
        // 过滤的条件
        if (StrUtil.isNotEmpty(productCategoryName)) {
            searchBuilder.filter(new String[]{"productCategoryName=" + productCategoryName});
        }
        // 排序的条件
        if (order != null) {
            if (order == 0) {
                searchBuilder.sort(new String[]{"price:asc"});
            } else if (order == 1) {
                searchBuilder.sort(new String[]{"price:desc"});
            }
        }
        Searchable searchable = searchClient.index(MEILISEARCH_INDEX).search(searchBuilder.build());
        return ResponseEntity.ok(searchable);
    }
}
