package com.jxp.easyes.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.Score;
import cn.easyes.common.enums.FieldType;
import cn.easyes.common.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 搜索商品的信息
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:48
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@IndexName(value = "pms", shardsNum = 1, replicasNum = 0)
public class EsProduct implements Serializable {
    private static final long serialVersionUID = -1L;
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;
    @IndexField(fieldType = FieldType.KEYWORD)
    private String productSn;
    private Long brandId;
    @IndexField(fieldType = FieldType.KEYWORD)
    private String brandName;
    private Long productCategoryId;
    @IndexField(fieldType = FieldType.KEYWORD)
    private String productCategoryName;
    private String pic;
    @IndexField(fieldType = FieldType.TEXT, analyzer = "ik_max_word")
    private String name;
    @IndexField(fieldType = FieldType.TEXT, analyzer = "ik_max_word")
    private String subTitle;
    @IndexField(fieldType = FieldType.TEXT, analyzer = "ik_max_word")
    private String keywords;
    private BigDecimal price;
    private Integer sale;
    private Integer newStatus;
    private Integer recommandStatus;
    private Integer stock;
    private Integer promotionType;
    private Integer sort;
    @IndexField(fieldType = FieldType.NESTED, nestedClass = EsProductAttributeValue.class)
    private List<EsProductAttributeValue> attrValueList;
    @Score
    private Float score;
}
