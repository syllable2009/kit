package com.jxp.autoassign.assignstrategy;

import java.util.List;

import com.jxp.autoassign.Customer;

/**
 * @author jiaxiaopeng
 * Created on 2025-01-06 16:04
 */
public interface AssignStrategy {
    Customer selectOne(List<Customer> customerList);
}
