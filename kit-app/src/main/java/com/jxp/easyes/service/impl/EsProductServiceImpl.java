package com.jxp.easyes.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jxp.easyes.domain.EsProduct;
import com.jxp.easyes.mapper.EsProductMapper;
import com.jxp.easyes.service.EsProductService;

import cn.hutool.core.collection.CollUtil;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:51
 */
@Service
public class EsProductServiceImpl implements EsProductService {

    @Autowired
    private EsProductMapper esProductMapper;

    @Override
    public Integer importAll(List<EsProduct> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            return 0;
        }
        return esProductMapper.insertBatch(dataList);
    }

    @Override
    public void delete(Long id) {
        esProductMapper.deleteById(id);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public EsProduct create(Long id) {
        EsProduct product = EsProduct.builder()
                .id(id)
                .brandId(id)
                .productSn("4566690")
                .productCategoryId(1L)
                .productCategoryName("3C数码")
                .name("耳机1")
                .keywords("索尼耳机")
                .price(BigDecimal.valueOf(100))
                .sort(99)
                .build();
        esProductMapper.insert(product);
        return product;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            esProductMapper.deleteBatchIds(ids);
        }
    }
}
