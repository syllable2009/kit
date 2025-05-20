package com.jxp.meili.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-20 11:32
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private Long aid;

    private String uid;

    private String name;
}
