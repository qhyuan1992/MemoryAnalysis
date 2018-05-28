package com.memory.analysis.utils;

import java.util.TreeSet;

/**
 * 保存RetainedSize最大的前50个对象实例
 */

public class StableList<E> extends TreeSet<E>{
    private static final int MAX_NUM = 50;


    @Override
    public boolean add(E e) {
        if (size() >= MAX_NUM) {
            pollLast();
        }
        return super.add(e);
    }
}
