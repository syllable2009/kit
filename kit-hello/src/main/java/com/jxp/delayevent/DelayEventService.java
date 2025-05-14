package com.jxp.delayevent;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件是独立还是续签的方式由业务控制，可以抽象封装一层
 * 延迟消息设置的时候要结合业务，存在一些极限场景：比如会话超时时间为2分钟，延迟2分钟结束，恰好2分钟的续期消息到了。。。
 * @author jiaxiaopeng
 * Created on 2025-05-14 14:57
 */
@Slf4j
@Service
public class DelayEventService {


    private static final String PREFIX_EVENT_TIMEOUT_TIME = "delay_event_";
    //延迟事件的reids过期时间的冗余值 2小时
    private static final long TIMEOUT_SETTING_EXPIRE_GAP_SEC = 2 * 60 * 60;

    /**
     * 延迟消息的处理逻辑设置
     */
    @Resource
    private Map<DelayEventType, DelayEventHandle> delayEventProcessorMap;

    private static String getDelayEventKey(DelayEventType eventType, String uniqueId) {
        return PREFIX_EVENT_TIMEOUT_TIME + eventType + ":" + uniqueId;
    }

    public Boolean setOrFreshDelayTime(DelayEvent event) {
        log.info("setOrFreshDelayTime start,event:{}", JSONUtil.toJsonStr(event));
        if (StringUtils.isBlank(event.getUniqueId()) || null == event.getEventType()) {
            log.error("setOrFreshDelayTime return,参数不合法,event:{}", JSONUtil.toJsonStr(event));
            return false;
        }
        // 1.参数校验
        final long current = System.currentTimeMillis();
        Long targetTime = null;
        Long delayToTime = event.getDelayMill();
        if (null != delayToTime) {
            targetTime = current + delayToTime;
        } else {
            // 计算出延迟多长时间
            targetTime = event.getDelayToTimestamp();
        }

        // 2.判断是否小于当前时间
        if (null == targetTime || targetTime.compareTo(current) < 0) {
            log.error("setOrFreshDelayTime return,目标时间为空或者小于当前时间,targetTime:{},current:{}", targetTime, current);
            return false;
        }

        if (null == event.getTimeStamp()) {
            event.setTimeStamp(current);
        }

        // 3.重新计算并加入redis中
        final String delayEventKey = getDelayEventKey(event.getEventType(), event.getUniqueId());
        final long delayMill = targetTime - System.currentTimeMillis();
        long expireTime = TIMEOUT_SETTING_EXPIRE_GAP_SEC + delayMill / 1000;

        /**
         ksRedisCommands.setex(delayEventKey, expireTime, String.valueOf(event.getTimeStamp()));

         // 4.底层发到延迟的队列中发送延时消息消息
         MqMessage mqMessage = msgProducer.createMsgBuilder(JsonUtils.objToJson(event), true)
         .withDelay(Duration.ofMillis(delayMill)).build();
         MqSyncSendResult sendResult = msgProducer.sendSync(mqMessage);
         if (!sendResult.isSuccess()) {
         log.error("setOrFreshDelayTime send mq failed,event:{}", JSONUtil.toJsonStr(event));
         return false;
         }*/
        return true;
    }

    public Boolean deleteDelayEvent(DelayEventType eventType, String uniqueId) {
        log.info("deleteDelayEvent eventType:{},uniqueId:{}", eventType, uniqueId);
        final String delayEventKey = getDelayEventKey(eventType, uniqueId);
//        Long delNum = ksRedisCommands.del(delayEventKey);
//        return delNum != null && delNum > 0;
        return true;
    }

    // 统一延迟事件处理，底层会找到对应业务的服务调用各自的triggerDelayEvent方法
    public void triggerDelayEvent(DelayEvent event) {
        log.info("triggerDelayEvent start,event:{}", JSONUtil.toJsonStr(event));
        // 判断事件是否被删除过期
        final String delayEventKey = getDelayEventKey(event.getEventType(), event.getUniqueId());
//        String value = ksRedisCommands.get(delayEventKey);
        String value = "";
        if (StringUtils.isBlank(value)) {
            log.info("triggerDelayEvent return,事件被删除失效,不再触发,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        // 判断事件是否被续签,区分续签了还是延迟执行了
        if (!StringUtils.equals(String.valueOf(event.getTimeStamp()), value)) {
            log.info("triggerDelayEvent return,事件已经被续期,忽略本次消费,event:{}", JSONUtil.toJsonStr(event));
            return;
        }

        // 删除标记
        final Boolean delResult = deleteDelayEvent(event.getEventType(), event.getUniqueId());
        if (!BooleanUtil.isTrue(delResult)) {
            log.info("triggerDelayEvent return,事件已经被并发处理,忽略本次消费,event:{}", JSONUtil.toJsonStr(event));
            return;
        }

        //开始执行
        final DelayEventHandle delayEventHandle = delayEventProcessorMap.get(event.getEventType());
        if (null == delayEventHandle) {
            log.error("triggerDelayEvent return,没有对应的执行器,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        try {
            delayEventHandle.triggerDelayEvent(event);
        } catch (Exception e) {
            //出现异常的时候，重试处理
            log.error("triggerDelayEvent exception,event:{}", JSONUtil.toJsonStr(event));
        }
    }
}
