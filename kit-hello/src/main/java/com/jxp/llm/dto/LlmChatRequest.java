package com.jxp.llm.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 11:53
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LlmChatRequest {

    private String bizType;
    private String bizId;
    private String model;
    private String user;
    private Boolean stream;
    private Map<String, String> config;

//    repeated Message messages = 4;
//    repeated KeyValuePair config = 7;
//    string limit_id = 8;
//    int64 run_id = 9;
//    int64 team_id = 10;
}
