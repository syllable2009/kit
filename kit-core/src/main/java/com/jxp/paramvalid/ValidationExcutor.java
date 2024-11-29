package com.jxp.paramvalid;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 14:20
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationExcutor<T> {
    private List<Validation<T>> stepList = new ArrayList<Validation<T>>();

    public ValidationExcutor addValidation(Validation<T> next) {
        stepList.add(next);
        return this;
    }

    public ValidationResult<T> checkAllWhenErr(T t) {
        if (stepList == null || stepList.size() == 0) {
            return null;
        }
        ValidationResult<T> validate = null;
        for (Validation v : stepList) {
            validate = v.validate(t);
            if (validate != null && validate.notValid()) {
                return validate;
            }
        }
        return null;
    }

    public List<ValidationResult<T>> checkAll(T t) {
        List<ValidationResult<T>> ret = new ArrayList<>();
        if (stepList == null || stepList.size() == 0) {
            return ret;
        }
        stepList.forEach(e -> {
            ValidationResult<T> validate = e.validate(t);
            if (validate.notValid()) {
                ret.add(validate);
            }
        });
        return ret;
    }
}
