package com.jxp.hotline.mq;

import org.springframework.stereotype.Component;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-27 12:05
 */
@Component
public class PulsarProducer {

//    @Autowired
//    private PulsarTemplate<String> stringTemplate;

    public void sendStringMessageToPulsarTopic(String str) throws Exception {
//        stringTemplate.send(STRING_TOPIC, str);
    }
}
