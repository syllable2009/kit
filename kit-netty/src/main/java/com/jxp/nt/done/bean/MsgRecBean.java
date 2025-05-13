package com.jxp.nt.done.bean;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 消息接受实体
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:06
 */
@SuperBuilder
@Data
public class MsgRecBean extends BaseBean {
    //发送人ID
    private Integer fromUserId;
    //发送消息
    private String msg;
}
