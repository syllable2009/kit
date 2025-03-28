package com.jxp.tinystruct;

import com.jxp.tinystruct.annotation.Action;
import com.jxp.tinystruct.annotation.Param;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 10:47
 */
public class Example {


    public void init() {
        // TODO Auto-generated method stub
    }

    public String version() {
        return "1.0";
    }

    @Action("praise")
    public String praise() {
        return "Praise the Lord!";
    }

    @Action("say")
    public String say() throws Exception {
//        if (null != getContext().getAttribute("--words"))
//            return getContext().getAttribute("--words").toString();
        throw new RuntimeException("Could not find the parameter <i>words</i>.");
    }

    // http://localhost:8089/?q=say/%E8%B5%9E%E7%BE%8E%E4%B8%BBwww%EF%BC%81
    // 编译后的方法参数名字会被擦除变成arg0，利用反射获取，需要使用注解或者其他办法
    @Action(value = "say", params = {
            @Param(value = "words", description = "words参数", optional = true, init = "默认值")
    })
    public String say(@Param("words") String words) {
        return words;
    }
}
