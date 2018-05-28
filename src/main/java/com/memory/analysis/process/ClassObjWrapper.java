package com.memory.analysis.process;

import com.memory.analysis.utils.FormatUtil;
import com.squareup.haha.perflib.ClassObj;

public class ClassObjWrapper implements SortableObject{
    // 类名
    public ClassObj classObj;
    // 类的对象数量
    public long instanceCount;
    // 引用内存
    public long retainedHeapSize;

    public ClassObjWrapper(ClassObj classObj) {
        this.classObj = classObj;
    }

    @Override
    public long getSize() {
        return instanceCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(classObj.getClassName())
                .append(" has instances ")
                .append(instanceCount)
                .append(" retained ")
                .append(FormatUtil.formatByteSize(retainedHeapSize))
                .append("\n");
        return builder.toString();
    }
}
