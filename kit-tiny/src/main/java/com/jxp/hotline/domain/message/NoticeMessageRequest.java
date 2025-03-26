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
public class NoticeMessageRequest extends SendMessageRequest {

    private NoticeContent notice;

    public NoticeMessageRequest() {
        super("notice");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    static class NoticeContent {
        private String content;
        private String zhCN;
        private String enUS;
    }
}
