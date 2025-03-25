package com.jxp.hotline.domain.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-25 16:19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferRuleItemDTO {
    /**
     *     KEYWORDS("keywords", "关键词"),
     *     EMPLOYEE_TYPE("EMPLOYEE_TYPE", "员工性质"),
     *     POSITION_CLASS("POSITION_CLASS", "工作性质"),
     *     EMPLOYEE_DEPARTMENT("EMPLOYEE_DEPARTMENT", "员工所属部门"),
     *     CURRENT_TIME("CURRENT_TIME", "当前时间"),
     *     WELFARE_LABEL("WELFARE_LABEL", "福利标签"),
     *     FOLOCATION("FOLOCATION", "工作地点"),
     *     SOURCE_TENANT("SOURCE_TENANT", "来源租户"),
     *     USER_LABEL("USER_LABEL", "用户标签");
     */
    private String type;
    /**
     *     和type的对应关系可以用枚举来描述出来
     *     PRECISE_MATCH("preciseMatch", "精准匹配"),
     *     FUZZY_MATCH("fuzzy_match", "模糊匹配"),
     *     EQ("eq", "等于"),
     *     NE("ne", "不等于"),
     *     BELONGS("belongs", "属于"),
     *     CONTAINS("contains", "包含");
     */
    private String matchingRule;
    // key=value，简单的一次性缓存
    private Map<String, String> condition;
}
