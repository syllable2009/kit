责任链模式，用于复杂的参数业务逻辑校验
这种方式可以优雅的将验证逻辑拆分到单独的类中，如果添加新的验证逻辑，只需要添加新的类，然后组装到“校验链”中。但是在我看来，
这比较适合于用于校验相对复杂的场景，如果只是简单的校验就完全没必要这么做了，反而会增加代码的复杂度。
@Override
public void validate(SignUpCommand command) {
    validateCommand(command); // will throw an exception if command is not valid
    validateUsername(command.getUsername()); // will throw an exception if username is duplicated
    validateEmail(commend.getEmail()); // will throw an exception if email is duplicated
}
改为
new DefaultSignUpValidationService().valid();


