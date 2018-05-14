package com.memory.analysis.utils;

import com.squareup.haha.perflib.Instance;

import java.util.LinkedList;
import java.util.List;

/**
 * 保存RetainedSize最大的前50个对象实例
 */
public class StableList {
    private static final int MAX_NUM = 50;
    private List<Instance> list = new LinkedList<>();

    public void add(Instance e) {
        if (list.size() >= MAX_NUM) {
            list.remove(list.size() - 1);
        }
        list.add(e);
        list.sort((i1, i2) -> Long.compare(i2.getTotalRetainedSize(), i1.getTotalRetainedSize()));
    }

    public int size() {
        return list.size();
    }

    public Instance get(int i) {
        return list.get(i);
    }
}
