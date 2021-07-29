package com.kamimi.lcalendar.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成器，简单的递增版
 */
public class IdGenerator {

    private final AtomicInteger counter;

    private IdGenerator(int initId) {
        counter = new AtomicInteger(initId);
    }

    public static IdGenerator create(int initId) {
        return new IdGenerator(initId);
    }

    public int next() {
        return counter.incrementAndGet();
    }

}
