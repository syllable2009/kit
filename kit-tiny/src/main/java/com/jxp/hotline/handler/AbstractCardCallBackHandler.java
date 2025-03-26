package com.jxp.hotline.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.jxp.hotline.domain.dto.CardInfo;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 15:45
 */
@Slf4j
public abstract class AbstractCardCallBackHandler<T> {


    protected Map<String, BiFunction<CardInfo, Object, String>> attributeActions = new HashMap<>();


    public AbstractCardCallBackHandler() {
        initializeActions();
    }

    protected abstract void initializeActions(

    );

    public abstract String getBizId();

    protected abstract Class<T> getParamType();

    public String triggerAction(String actionId, CardInfo value, Object entity) {
        log.info("mixcard triggerAction,actionId:{},param:{}", actionId, JSONUtil.toJsonStr(entity));
//        T cardParam = JsonUtils.jsonToObj(entity.getVariable(), getParamType());
        BiFunction<CardInfo, Object, String> action = attributeActions.get(actionId);
        if (action != null) {
            String apply = action.apply(value, entity);
            log.info("triggerAction,actionId:{},card:{}", actionId, apply);
            return apply;
        } else {
            log.info("No action found for attribute: {}", actionId);
            // 给card 发送fail的toast
            return IdUtil.fastSimpleUUID();
        }
    }
}
