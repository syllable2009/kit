package com.jxp.es7;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.es7.index.Account;
import com.jxp.es7.repository.AccountMapper;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-26 17:27
 */
@RequestMapping("/es")
@RestController
@Slf4j
public class EsTestController {

    @Resource
    ElasticsearchRestTemplate template;

    @Resource
    AccountMapper accountMapper;

    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {
        log.info("elasticsearchRestTemplate:{},accountMapper:{}", template, accountMapper);
        IndexOperations idxOpt = template.indexOps(Account.class);
        // 索引是否存在
        boolean idxExist = idxOpt.exists();
        log.info("索引是否存在,idxExist:{}", idxExist);
        // 创建索引
        if (BooleanUtil.isFalse(idxExist)) {
            boolean createSuccess = idxOpt.createWithMapping();
            log.info("创建索引,result:{}", createSuccess);
        }
        // 删除索引
        // boolean deleted = idxOpt.delete();
        return ResponseEntity.ok("ok");
    }

    @GetMapping(value = "/add")
    public ResponseEntity<?> add() {
        Account account = new Account();
        final String uid = IdUtil.fastSimpleUUID();
        account.setId(uid);
        account.setLastname(RandomUtil.randomNumbers(8));
        // id存在了就是覆盖，否则为保存
        template.save(account);
        // 修改，用的是es的_update
//        template.delete(account);
        Account a = template.get(uid, Account.class);

        return ResponseEntity.ok(a);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(accountMapper.findAll());
    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> search() {
        // 搜索 firstname = Amber AND age = 32
        Criteria criteria = new Criteria();
        criteria.and(new Criteria("firstname").is("Amber"));
        criteria.and(new Criteria("age").is(32));

        // 分页
        int pageNum = 1; // 页码
        int pageSize = 20; // 每页数量
        Query query = new CriteriaQueryBuilder(criteria)
                .withSort(Sort.by(new Order(Sort.Direction.ASC, "age"))) // 排序字段1
                .withSort(Sort.by(new Order(Sort.Direction.DESC, "balance"))) // 排序字段1
                .withPageable(PageRequest.of(pageNum - 1, pageSize)) // 浅分页
                // 不需要查询的字段
                .withSourceFilter(new FetchSourceFilterBuilder().withExcludes("email", "address").build())
                .build();
        return ResponseEntity.ok(template.search(query, Account.class).getSearchHits());
    }
}
