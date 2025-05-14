package com.jxp.nt.done.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:02
 */
@SuperBuilder
@Data
public abstract class BaseBean implements Serializable {
    // 事件类型
    private String type;
    // 唯一id
    private String uid;
    // 发生时间
    private LocalDateTime time;
}
