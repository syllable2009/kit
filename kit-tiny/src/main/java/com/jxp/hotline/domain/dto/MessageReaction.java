package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:48
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageReaction {
    // add del
    private String reactionType;

    private String emojiId;
}
