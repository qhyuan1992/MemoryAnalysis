package com.memory.analysis.utils;

import com.memory.analysis.process.SortableObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 保存RetainedSize最大的前50个对象实例
 */
public class StableList<T extends SortableObject> {
    private static final int MAX_NUM = 50;
    public List<T> list = new LinkedList<>();

    public void add(T e) {
        if (list.size() >= MAX_NUM) {
            list.remove(list.size() - 1);
        }
        list.add(e);
        list.sort((i1, i2) -> Long.compare(i2.getSize(), i1.getSize()));
    }

    public int size() {
        return list.size();
    }

    public T get(int i) {
        return list.get(i);
    }


}
