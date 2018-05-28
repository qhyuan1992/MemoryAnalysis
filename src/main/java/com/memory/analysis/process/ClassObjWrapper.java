package com.memory.analysis.process;

import com.memory.analysis.utils.FormatUtil;
import com.squareup.haha.perflib.ClassObj;

import static com.memory.analysis.utils.FormatUtil.realEqual;

public class ClassObjWrapper implements SortableObject, Comparable{
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassObjWrapper that = (ClassObjWrapper) o;

        // 相差5K以上认为是不同的对象
        if (Math.abs(retainedHeapSize - that.retainedHeapSize) > 5000) return false;
        return classObj != null ? realEqual(classObj.getClassName(), that.classObj.getClassName()) : that.classObj == null;
    }

    @Override
    public int hashCode() {
        return classObj != null ? classObj.getClassName().substring(0, classObj.getClassName().indexOf(".")).hashCode() : 0;
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

    @Override
    public int compareTo(Object o) {
        ClassObjWrapper that = (ClassObjWrapper) o;
        return Long.compare(that.getSize(), this.getSize());
    }
}
