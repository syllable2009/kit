package com.jxp.flows.infs;

import java.util.concurrent.atomic.AtomicInteger;

import com.jxp.flows.domain.FlowContext;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 20:49
 */
@FunctionalInterface
public interface IPredicate {

    // 条件语句也封装成INode
    boolean apply(INode iNode, FlowContext context);

    IPredicate ALWAYS_TRUE = (k, v) -> true;
    IPredicate ALWAYS_FALSE = (k, v) -> false;
//    IPredicate COMPLETED = result -> result.getState().equals(NodeState.COMPLETED);
//    IPredicate FAILED = result -> result.getState().equals(NodeState.FAILED);


    class TimesPredicate implements IPredicate {

        private int times;

        private AtomicInteger counter = new AtomicInteger();

        public TimesPredicate(int times) {
            this.times = times;
        }

        @Override
        public boolean apply(INode inode, FlowContext context) {
            return counter.incrementAndGet() != times;
        }

        public static TimesPredicate times(int times) {
            return new TimesPredicate(times);
        }
    }
}
