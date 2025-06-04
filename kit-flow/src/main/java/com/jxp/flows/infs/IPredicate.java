package com.jxp.flows.infs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 20:49
 */
@FunctionalInterface
public interface IPredicate {

    // 条件语句也封装成INode
    boolean apply(INode iNode);

    IPredicate ALWAYS_TRUE = result -> true;
    IPredicate ALWAYS_FALSE = result -> false;
//    IPredicate COMPLETED = result -> result.getState().equals(NodeState.COMPLETED);
//    IPredicate FAILED = result -> result.getState().equals(NodeState.FAILED);


    class TimesPredicate implements IPredicate {

        private int times;

        private AtomicInteger counter = new AtomicInteger();

        public TimesPredicate(int times) {
            this.times = times;
        }

        @Override
        public boolean apply(INode inode) {
            return counter.incrementAndGet() != times;
        }

        public static TimesPredicate times(int times) {
            return new TimesPredicate(times);
        }
    }
}
