package com.jxp.es7.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.jxp.es7.index.Account;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-26 17:25
 */
public interface AccountMapper extends ElasticsearchRepository<Account, Long> {
}
