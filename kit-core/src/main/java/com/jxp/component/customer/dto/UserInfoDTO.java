package com.jxp.component.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 17:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoDTO {

    private String userId;

    private String userType;

    private String userName;

    private String name;

}
