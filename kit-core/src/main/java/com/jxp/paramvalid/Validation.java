package com.jxp.paramvalid;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 11:05
 */

public interface Validation<T> {
    ValidationResult<T> validate(T t);
}
