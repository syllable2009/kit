package com.jxp.nt.done.bean;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-13 10:04
 */

@SuperBuilder
@Data
public class LoginReqBean extends BaseBean {
    //用户ID
    private String userId;
    //用户名称
    private String username;
}
