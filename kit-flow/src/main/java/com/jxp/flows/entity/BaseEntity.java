package com.jxp.flows.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-23 10:14
 */

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseEntity {

//    @TableId(type = IdType.AUTO)
    private Long id;

    private String uid;

//    @TableField("state")
    private Integer state;
//
//    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
//
//    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Long updateTime;

    private String createId;

    private String updateId;
}
