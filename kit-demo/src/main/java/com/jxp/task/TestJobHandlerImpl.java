package com.jxp.task;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import xyz.erupt.job.handler.EruptJobHandler;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-20 15:23
 */
@Service
@Slf4j
public class TestJobHandlerImpl implements EruptJobHandler {

    @Override
    public String exec(String code, String param) {
        log.info("定时任务已经执行，code:{},param:{},time:{}", code, param, LocalDateTime.now());
        return "success";
    }
}
