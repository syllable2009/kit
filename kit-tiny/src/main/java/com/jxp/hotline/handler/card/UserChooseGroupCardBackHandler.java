package com.jxp.hotline.handler.card;

import org.springframework.stereotype.Component;

import com.jxp.hotline.domain.dto.CardInfo;
import com.jxp.hotline.handler.AbstractCardCallBackHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 15:49
 */
@Component
@Slf4j
public class UserChooseGroupCardBackHandler extends AbstractCardCallBackHandler<String> {
    @Override
    protected void initializeActions() {
        attributeActions.put("submit_button", this::submit);
        attributeActions.put("add_button", this::submit);
        attributeActions.put("del_button", this::submit);
    }

    @Override
    public String getBizId() {
        return "userChooseAssitantGroup";
    }

    @Override
    protected Class<String> getParamType() {
        return String.class;
    }

    private String submit(CardInfo info, Object entity) {
        // 处理方法
        return null;
    }
}
