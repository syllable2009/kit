package com.jxp.component.customer.service;

import com.jxp.component.customer.dto.MessageCallback;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:49
 */
public interface ApiService {

    void handleMessageCallback(MessageCallback messageCallback);
}
