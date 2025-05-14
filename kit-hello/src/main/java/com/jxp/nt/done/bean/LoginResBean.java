package com.jxp.nt.done.bean;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:05
 */
@SuperBuilder
@Data
public class LoginResBean extends BaseBean {
    //响应状态，0登录成功，1登录失败
    private Integer status;
    //响应信息
    private String msg;
    //用户ID
    private String userid;
}
