package com.memory.analysis.process;

import com.memory.analysis.db.factory.IFactory;

import java.util.concurrent.Callable;

/**
 * @author cainjiang
 * @date 2018/7/7
 */
public abstract  class BaseCallable implements Callable<Integer> {

    IFactory mFactory;

    public BaseCallable(IFactory factory) {
        this.mFactory = factory;
    }
}
