package com.jxp.meili;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.meili.domain.Music;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.model.DocumentQuery;
import com.meilisearch.sdk.model.SwapIndexesParams;
import com.meilisearch.sdk.model.TaskInfo;

import io.swagger.v3.oas.annotations.Operation;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-06 16:28
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${meilisearch.index}")
    private String MEILISEARCH_INDEX;

    @Autowired
    private Client searchClient;

    @Operation(summary = "search")
    @PostMapping("/search")
    public ResponseEntity search(@RequestParam String uid, @RequestParam String primaryKey) {
        searchClient.index("movies").search("American ninja");
        return ResponseEntity.ok(searchClient.updateIndex(uid, primaryKey));
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
