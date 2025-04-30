package com.jxp.easyes.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.collect.Lists;
import com.jxp.easyes.domain.EsProduct;
import com.jxp.easyes.mapper.EsProductMapper;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 21:29
 */

@SpringBootTest
class EsProductServiceTest {

    @Mock
    EsProductMapper esProductMapper;


    @InjectMocks
    EsProductService esProductService;

    @Test
    void importAll() {
        final List<EsProduct> dataList = Lists.newArrayList();
        dataList.add(EsProduct.builder()
                .id(1L)
                .brandId(1L)
                .productSn("4566690")
                .productCategoryId(1L)
                .productCategoryName("3C数码")
                .name("耳机1")
                .keywords("索尼耳机")
                .price(BigDecimal.valueOf(100))
                .sort(99)
                .build());
        esProductService.importAll(dataList);
    }

    @Test
    void delete() {
    }

    @Test
    void create() {
    }

    @Test
    void testDelete() {
    }
}