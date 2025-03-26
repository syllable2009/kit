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
public class ImageMessageRequest extends SendMessageRequest {

    private ImageContent image;

    public ImageMessageRequest() {
        super("image");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    static class ImageContent {
        private String mediaId;
        private String url;
    }
}
