package com.jxp.easyes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.jxp.easyes.domain.EsProduct;
import com.jxp.easyes.mapper.EsProductMapper;
import com.jxp.easyes.service.EsProductService;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:51
 */
@Service
public class EsProductServiceImpl implements EsProductService {

    @Autowired
    private EsProductMapper esProductMapper;

    @Override
    public int importAll() {
        List<EsProduct> esProductList = Lists.newArrayList();
        return esProductMapper.insertBatch(esProductList);
    }

    @Override
    public void delete(Long id) {
        esProductMapper.deleteById(id);
    }

    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        esProductMapper.insert(result);
        return result;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            esProductMapper.deleteBatchIds(ids);
        }
    }
}
