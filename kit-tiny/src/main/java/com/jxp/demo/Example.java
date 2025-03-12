package com.jxp.demo;

import com.jxp.system.annotation.Action;
import com.jxp.system.annotation.Argument;
import com.jxp.system.domain.Mode;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-24 17:47
 */
public class Example {


    public void init() {
        // TODO Auto-generated method stub
    }

    @Action("version")
    public String version() {
        return "1.0";
    }

    @Action("praise")
    public String praise() {
        return "Praise the Lord!";
    }

    // 编译后的方法参数名字会被擦除变成arg0，利用反射获取，需要使用注解或者其他办法
    // 严格以Argument的顺序取值,还有中方式是每个方法强制把上下文传递进来
    @Action(value = "say", description = "Say Action", options = {
            @Argument(key = "app", required = false, description = "Say something", value = "world")
    }, mode = Mode.All)
    public String say(String words) {
        return words;
    }

}
