package com.jxp.easyes.domain;

import java.io.Serializable;

import cn.easyes.annotation.IndexField;
import cn.easyes.common.enums.FieldType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 搜索商品的属性信息
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:49
 */
@Data
@EqualsAndHashCode
public class EsProductAttributeValue implements Serializable {
    private static final long serialVersionUID = 1L;
    @IndexField(fieldType = FieldType.LONG)
    private Long id;
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long productAttributeId;
    //属性值
    @IndexField(fieldType = FieldType.KEYWORD)
    private String value;
    //属性参数：0->规格；1->参数
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer type;
    //属性名称
    @IndexField(fieldType=FieldType.KEYWORD)
    private String name;
}
