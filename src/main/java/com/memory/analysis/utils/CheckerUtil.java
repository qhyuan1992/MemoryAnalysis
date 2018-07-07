package com.memory.analysis.utils;

import com.memory.analysis.process.InstanceWrapper;

import java.util.List;

/**
 * @author cainjiang
 * @date 2018/5/30
 */
public class CheckerUtil {
    public static boolean isEmpty(InstanceWrapper instanceWrapper) {
        return instanceWrapper.classObj != null && instanceWrapper.instance != null && instanceWrapper.leakTrace != null ? false : true;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() < 1 ? true : false;
    }
}
