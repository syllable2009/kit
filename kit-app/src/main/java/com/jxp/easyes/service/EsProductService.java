package com.jxp.easyes.service;

import java.util.List;

import com.jxp.easyes.domain.EsProduct;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:52
 */
public interface EsProductService {

    int importAll();

    void delete(Long id);

    EsProduct create(Long id);

    void delete(List<Long> ids);
}
