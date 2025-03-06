package com.jxp.tinystruct.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 11:31
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommandParam<K, V> {

    private K key;
    private V value; // 默认值
    private String description;
    private boolean optional;
}
