package com.memory.analysis.utils;

import com.memory.analysis.process.InstanceWrapper;

/**
 * @author cainjiang
 * @date 2018/5/30
 */
public class CheckerUtil {
    public static boolean isEmpty(InstanceWrapper instanceWrapper) {
        return instanceWrapper.classObj != null && instanceWrapper.instance != null && instanceWrapper.leakTrace != null ? false : true;
    }
}
