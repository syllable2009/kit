package com.jxp.hotline.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 11:19
 */
@AllArgsConstructor
@Data
@SuperBuilder
public class TextMessageRequest extends SendMessageRequest {

    private TextContent text;

    public TextMessageRequest() {
        super("text");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    static class TextContent {
        private String content;
    }
}
