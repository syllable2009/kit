package com.jxp.dictionary.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-22 15:06
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictionaryRelation {
    private Long aid;
    private String uid;
    private String bizCode;
    private Long dictAid;
    private String dictUid;
    private Long itemAid;
    private String itemUid;
    // 权重，用于排序，从小到大
    private Integer weight;
    private String groupId;
    private String treeId;
    private Long pAid;
    private String pUid;
}
