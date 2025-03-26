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
public class FileMessageRequest extends SendMessageRequest {

    private FileContent file;

    public FileMessageRequest() {
        super("file");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    static class FileContent {
        private String mediaId;
        private String type;
        private long size;
        private String title;
    }
}
