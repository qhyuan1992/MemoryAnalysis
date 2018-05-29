package com.memory.analysis.process;

import com.memory.analysis.utils.FormatUtil;

import java.util.List;

public class ActivityWrapper implements Comparable, SortableObject{
    public ClassObjWrapper classObjWrapper;
    public List<InstanceWrapper> instanceWrapperList;

    @Override
    public int compareTo(Object o) {
        ActivityWrapper that = (ActivityWrapper) o;
        return Long.compare(that.getSize(), this.getSize());
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(classObjWrapper.classObj.getClassName())
                .append(" has instances ")
                .append(classObjWrapper.instanceCount)
                .append(" retained ")
                .append(FormatUtil.formatByteSize(classObjWrapper.retainedHeapSize));
        if (instanceWrapperList != null) {
            for (InstanceWrapper instanceWrapper : instanceWrapperList) {
                builder.append("===================")
                        .append(instanceWrapper.toString());
            }
        }
        return builder.toString();
    }
}
