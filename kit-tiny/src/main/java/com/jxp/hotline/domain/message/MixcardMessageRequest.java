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
public class MixcardMessageRequest extends SendMessageRequest {

    private Card mixCard;

    public MixcardMessageRequest() {
        super("mixCard");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    static class Card {
        private String toast;
        private String operation;
        private String messageKey;
        private Long operatorId;
        private String blocksChangeType;
        private Long timestamp;
    }
}
