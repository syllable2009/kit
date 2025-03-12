package com.jxp.demo;

import com.jxp.system.annotation.Action;

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

//    @Action("say")
//    public String say() throws ApplicationException {
//        if (null != getContext().getAttribute("--words"))
//            return getContext().getAttribute("--words").toString();
//
//        throw new ApplicationException("Could not find the parameter <i>words</i>.");
//    }
//
//    // http://localhost:8089/?q=say/%E8%B5%9E%E7%BE%8E%E4%B8%BBwww%EF%BC%81
//    // 编译后的方法参数名字会被擦除变成arg0，利用反射获取，需要使用注解或者其他办法
//    @Action("say")
//    public String say(String words) {
//        return words;
//    }


}
