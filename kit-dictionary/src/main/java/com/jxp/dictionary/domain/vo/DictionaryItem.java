package com.jxp.dictionary.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-22 11:44
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DictionaryItem {
    private Long aid;
    private String uid;
    private String code;
    private String nameCN;
    private String nameEN;
}
