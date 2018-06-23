package com.memory.analysis.process;

import com.memory.analysis.leak.AnalysisResult;
import com.memory.analysis.leak.LeakTrace;
import com.memory.analysis.utils.FormatUtil;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.Instance;

import java.util.HashSet;
import java.util.Set;

import static com.memory.analysis.utils.FormatUtil.realEqual;

public class InstanceWrapper implements SortableObject, Comparable{
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

    public long id;

    private static Set<String> instanceExcluded = new HashSet();
    static {
        instanceExcluded.add("android.graphics.Bitmap");
    }

    public InstanceWrapper(Instance instance) {
        this.instance = instance;
    }

    public void fill(AnalysisResult analysisResult, long totalRetainedSize) {
        this.referenceChain = analysisResult;
        this.found = analysisResult.leakFound;
        this.classObj = instance.getClassObj();
        this.leakTrace = analysisResult.leakTrace;
        this.retainedHeapSize = analysisResult.retainedHeapSize;
        this.sizeRation = analysisResult.retainedHeapSize*1.0/totalRetainedSize;
        this.id = instance.getId();
    }

    @Override
    public long getSize() {
        return retainedHeapSize;
    }

    /**
     * className相同  大小相差不大时不用继续添加
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstanceWrapper that = (InstanceWrapper) o;

        if (!instanceExcluded.contains(that.instance.getClassObj().getClassName()) && realEqual(this.instance.getClassObj().getClassName(), that.instance.getClassObj().getClassName())) { // 同一个类型只需要一个，Bitmap除外
            return true;
        }
        return this.instance.getClassObj() != null ? realEqual(this.instance.getClassObj().getClassName(), that.instance.getClassObj().getClassName()) : that.instance.getClassObj() == null;
    }

    @Override
    public int hashCode() {
        return classObj != null ? classObj.getClassName().substring(0, classObj.getClassName().lastIndexOf(".")).hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (found) {
            builder.append(classObj.getClassName())
                    .append(" reference ")
                    .append(FormatUtil.formatByteSize(retainedHeapSize))
                    .append(" ration:").append(FormatUtil.formatPercent(sizeRation))
                    .append(" ")
                    .append(FormatUtil.formatAddr(id))
                    .append("\n")
                    .append(leakTrace.toString())
                    .append("\n");
        }
        return builder.toString();

    }

    @Override
    public int compareTo(Object o) {
        InstanceWrapper that = (InstanceWrapper) o;
        return Long.compare(that.getSize(), this.getSize());
    }

}
