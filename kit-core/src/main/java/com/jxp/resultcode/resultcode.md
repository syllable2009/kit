
自动装配CommonExceptionCode中定义的错误双语对象。
如果springboot需要扩展，可以开启自动注入。
error-code-extension:
    enable: true

在spring管理的类和属性上标记注解IExceptionCode即可。

使用： ResultCode.of(0);