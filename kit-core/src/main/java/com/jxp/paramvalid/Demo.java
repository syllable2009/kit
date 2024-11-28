package com.jxp.paramvalid;

import java.util.List;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 11:09
 */

public class Demo {

    public static void main(String[] args) {
        // 要验证的内容
        String str = "abc";
        // build会导致初始化失效
//        final ValidationExcutor<String> excutor = ValidationExcutor.<String>builder().build();
        final ValidationExcutor<String> excutor = new ValidationExcutor<>();
        excutor.addValidation(new ConvertValidation<>())
                .addValidation(new FomatValidation())
                .addValidation(new LengthValidation());
        ValidationResult<String> validationResult = excutor.checkAllWhenErr(str);
        final List<ValidationResult<String>> validationResults = excutor.checkAll(str);
    }

    @Data
    static class LengthValidation<T> implements Validation<T> {

        @Override
        public ValidationResult validate(T s) {
            return ValidationResult.<String>builder()
                    .param("参数不能为空")
                    .build();
        }
    }

    static class FomatValidation<T> implements Validation<T> {
        @Override
        public ValidationResult validate(T s) {
            return ValidationResult.<String>builder()
                    .param("格式化失败")
                    .build();
        }
    }

    static class ConvertValidation<T> implements Validation<T> {
        @Override
        public ValidationResult validate(T s) {
            return ValidationResult.<String>builder()
                    .param("转化为数字失败")
                    .build();
        }
    }
}
