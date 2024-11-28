package com.jxp.exception;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 16:51
 */
public class CommonExceptionCode {

    @IExceptionCode(
            code = 0,
            zhCN = "成功",
            enUS = "ok"
    )
    int OK;

    @IExceptionCode(
            code = 1,
            zhCN = "失败",
            enUS = "Fail"
    )
    int FAIL;
}
