package com.jxp.tool;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

/**
 * @author jiaxiaopeng
 * Created on 2023-07-04 17:43
 */
public class ValidationUtils {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证数据
     *
     * @param object 数据
     */
    public static void validate(Object object) throws ValidationException {

        Set<ConstraintViolation<Object>> validate = VALIDATOR.validate(object);

        // 验证结果异常
        throwParamException(validate);
    }

    /**
     * 验证数据(分组)
     *
     * @param object 数据
     * @param groups 所在组
     */
    public static void validate(Object object, Class<?>... groups) throws ValidationException {

        Set<ConstraintViolation<Object>> validate = VALIDATOR.validate(object, groups);

        // 验证结果异常
        throwParamException(validate);
    }

    /**
     * 验证数据中的某个字段(分组)
     *
     * @param object 数据
     * @param propertyName 字段名称
     */
    public static void validate(Object object, String propertyName) throws ValidationException {
        Set<ConstraintViolation<Object>> validate = VALIDATOR.validateProperty(object, propertyName);

        // 验证结果异常
        throwParamException(validate);

    }

    /**
     * 验证数据中的某个字段(分组)
     *
     * @param object 数据
     * @param propertyName 字段名称
     * @param groups 所在组
     */
    public static void validate(Object object, String propertyName, Class<?>... groups) throws ValidationException {

        Set<ConstraintViolation<Object>> validate = VALIDATOR.validateProperty(object, propertyName, groups);

        // 验证结果异常
        throwParamException(validate);

    }

    /**
     * 验证结果异常
     *
     * @param validate 验证结果
     */
    private static void throwParamException(Set<ConstraintViolation<Object>> validate) throws ValidationException {
        if (validate.size() > 0) {
            //            List<String> fieldList = new LinkedList<>();
            //            List<String> msgList = new LinkedList<>();
            StringBuffer sb = new StringBuffer();
            for (ConstraintViolation<Object> next : validate) {
                //                fieldList.add(next.getPropertyPath().toString());
                //                msgList.add(next.getMessage());
                sb.append(next.getPropertyPath().toString())
                        .append(":")
                        .append(next.getMessage())
                        .append(",");
            }
            throw new ValidationException(sb.deleteCharAt(sb.length() - 1).toString());
        }
    }
}
