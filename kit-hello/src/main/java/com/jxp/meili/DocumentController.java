package com.jxp.meili;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.jxp.meili.domain.Music;
import com.jxp.meili.domain.Product;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.model.DocumentQuery;
import com.meilisearch.sdk.model.DocumentsQuery;
import com.meilisearch.sdk.model.Results;
import com.meilisearch.sdk.model.SwapIndexesParams;
import com.meilisearch.sdk.model.TaskInfo;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-06 16:28
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${meilisearch.index}")
    private String MEILISEARCH_INDEX;

    @Autowired
    private Client searchClient;


    @Operation(summary = "query")
    @GetMapping("/query")
    public Results<Music> query() {
        DocumentsQuery query = new DocumentsQuery()
                .setFilter(new String[]{"(rating > 3 AND (genres = Adventure OR genres = Fiction)) AND language = English"})
                .setFields(new String[]{"title", "genres", "rating", "language"})
                .setLimit(3);
        final Results<Music> musics = searchClient.index("musics").getDocuments(query, Music.class);
        return musics;
    }

    // uid为movies，primaryKey=id
    @Operation(summary = "get")
    @GetMapping("/get")
    public Results<Product> get() {
        DocumentsQuery query = new DocumentsQuery().setLimit(2)
//                .setFilter(new String[] {"genres = action"})
                ;
        final Results<Product> products = searchClient.index("products").getDocuments(query, Product.class);
        return products;
    }

    @Operation(summary = "add")
    @PostMapping("/add")
    public TaskInfo add() {
        final List<Music> dataList = Lists.newArrayList();
        final LocalDateTime now = LocalDateTime.now();
        dataList.add(Music.builder()
                .aid(1L)
                .uid("9856")
                .title("给我一首歌的时间")
                .description("给我一首歌的时间-jay")
                .poster("http://baidu.com")
                .genres(new String[]{"大众", "摇滚"})
                .singer(new String[]{"jay", "张三"})
                .createTime(now)
                .updateTime(now)
                .build());
        dataList.add(Music.builder()
                .aid(2L)
                .uid("9857")
                .title("什么是时间")
                .description("什么是时间-jxp")
                .poster("http://baidu.com")
                .genres(new String[]{"流行", "摇滚"})
                .singer(new String[]{"李四", "张三"})
                .createTime(now)
                .updateTime(now)
                .build());
        final TaskInfo musics = searchClient.index("musics").addDocuments(JSONUtil.toJsonStr(dataList));
        return musics;
    }


    @Operation(summary = "one")
    @GetMapping("/one")
    public ResponseEntity one(@RequestParam String aid) {
        final DocumentQuery query = new DocumentQuery();
        query.setFields(new String[]{"aid", "uid", "poster", "title", "description", "genres", "singer"});

        final Music one = searchClient.index("musics").getDocument(aid, query, Music.class);
        return ResponseEntity.ok(one);
    }

    @Operation(summary = "修改索引,无法更改索引的uid")
    @PostMapping("/update")
    public ResponseEntity update(@RequestParam String uid, @RequestParam String primaryKey) {
        return ResponseEntity.ok(searchClient.updateIndex(uid, primaryKey));
    }

    @Operation(summary = "刪除索引")
    @PostMapping("/delete")
    public ResponseEntity deleteIndex(@RequestParam String uid) {
        TaskInfo info = searchClient.deleteIndex(uid);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "交换索引，交换两个或多个索引的文档、设置和任务历史记录")
    @PostMapping("/swap")
    public ResponseEntity swap(@RequestParam String uid, @RequestParam String primaryKey) {
        SwapIndexesParams[] params =
                new SwapIndexesParams[]{
                        new SwapIndexesParams().setIndexes(new String[]{"indexA", "indexB"}),
                        new SwapIndexesParams().setIndexes(new String[]{"indexX", "indexY"})
                };
        TaskInfo task = searchClient.swapIndexes(params);
        return ResponseEntity.ok(task);
    }
}
