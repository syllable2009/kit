package com.jxp.tool;

import com.google.common.base.Stopwatch;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationTemplate {
    @SneakyThrows
    public <T> T invoke(Call<T> call) {
        boolean isSuccess = true;
        String errorCode = "0";
        Stopwatch stopwatch = Stopwatch.createStarted();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String className = this.getClass().getSimpleName();
        String requestStr = serialize(call.getRequest());
        String responseStr = null;
        try {
            T result = call.invoke();
            responseStr = serialize(result);
            call.check(result);
            log.info("class:{},method:{},cost:{},request:{},response:{}",
                    className,
                    methodName,
                    stopwatch.elapsed().toMillis(),
                    requestStr,
                    responseStr);
            return result;
        } catch (Exception e) {
            isSuccess = false;
            printErrorLog(stopwatch, methodName, className, requestStr, responseStr, e);
            throw e;
        } finally {
            log.info("{}|{}|{}|{}|{}",
                    className,
                    methodName,
                    stopwatch.elapsed().toMillis(),
                    isSuccess,
                    errorCode);
        }
    }

    private void printErrorLog(Stopwatch stopwatch, String methodName, String className, String requestStr,
            String responseStr, Exception e) {
        long cost = stopwatch.elapsed().toMillis();
        log.error("class:{}，method:{}，cost:{}，request:{}，response:{}",
                className,
                methodName,
                cost,
                requestStr,
                responseStr,
                e);
    }

    private <T> String serialize(T t) {
        try {
            if (t instanceof Message) {
                return JsonFormat.printer().print((Message) t);
            }
            return JacksonUtils.toJsonStr(t);
        } catch (Exception e) {
            log.error("Failed to serialize:", e);
            return null;
        }
    }

    public abstract static class Call<T> {

        private final Object request;

        protected Call(Object... request) {
            this.request = request;
        }

        public abstract T invoke() throws Exception;

        public void check(T t) {
        }

        public Object getRequest() {
            return request;
        }
    }
}