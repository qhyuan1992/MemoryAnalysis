package com.memory.analysis.process;

import com.memory.analysis.leak.AnalysisResult;
import com.memory.analysis.leak.LeakTrace;
import com.memory.analysis.utils.FormatUtil;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.Instance;

public class InstanceWrapper implements SortableObject {
    // 对象实例
    public Instance instance;
    // 对象引用链
    public AnalysisResult referenceChain;
    // true时才有意义
    public boolean found;
    //
    public ClassObj classObj;
    //
    public LeakTrace leakTrace;
    // 对象引用住的大小
    public long retainedHeapSize;
    // 引用内存占比
    public double sizeRation;

    public InstanceWrapper(Instance instance) {
        this.instance = instance;
    }

    @Override
    public long getSize() {
        return retainedHeapSize;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (found) {
            builder.append(classObj.getClassName())
                    .append(" reference ")
                    .append(FormatUtil.formatByteSize(retainedHeapSize))
                    .append(" ration:").append(FormatUtil.formatPercent(sizeRation))
                    .append("\n")
                    .append(leakTrace.toString())
                    .append("\n");
        }
        return builder.toString();

    }
}
