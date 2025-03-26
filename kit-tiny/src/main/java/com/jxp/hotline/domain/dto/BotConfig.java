package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 14:27
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BotConfig {

    //workflow agent
    private String botType;

    private String botId;

    private String botName;

    public boolean isAgent() {
        return "agent".equals(botType);
    }
}
