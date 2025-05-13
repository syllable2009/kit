package com.jxp.nt.done.bean;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:06
 */
@SuperBuilder
@Data
public class MsgReqBean extends BaseBean {
    //发送人ID
    private Integer fromUserId;
    //接受人ID
    private Integer toUserId;
    //发送消息
    private String msg;
}
