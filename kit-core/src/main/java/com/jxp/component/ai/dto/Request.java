package com.jxp.component.ai.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:48
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Request {

    private String username;
    private String userId;
    private Long timeStamp;
    private double amount;
    private Map<String, String> extraMap;
}
