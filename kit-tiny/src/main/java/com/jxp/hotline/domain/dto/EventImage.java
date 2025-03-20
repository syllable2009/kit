package com.jxp.hotline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 10:43
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventImage {
    private String messageType = "image";
    private Integer height;
    private Integer width;
    private Long contentLength;
    private String mediaId;
    private String downloadUrl;
    private String originName;
}
