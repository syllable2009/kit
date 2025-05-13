package com.jxp.nt.done.bean;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:06
 */
@SuperBuilder
@Data
public class MsgResBean extends BaseBean {
    //响应状态，0登录成功，1登录失败
    private Integer status;
    //响应信息
    private String msgKey;
}
