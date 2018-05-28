package com.memory.analysis.process;

import com.memory.analysis.leak.AnalysisResult;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;

import java.util.List;

public class InstanceAnalysis {

    public long totalRetainedSize;

    public StableList<InstanceWrapper> topInstanceList;

    private Snapshot snapshot;
    private final HeapAnalyzer heapAnalyzer;

    public InstanceAnalysis(Snapshot snapshot, HeapAnalyzer heapAnalyzer){
        this.snapshot = snapshot;
        this.heapAnalyzer = heapAnalyzer;
        topInstanceList = initTopInstance();
        findRefeference(topInstanceList);
    }

    private void findRefeference(StableList<InstanceWrapper> topInstanceList) {
        for (InstanceWrapper instanceWrapper: topInstanceList) {
            AnalysisResult analysisResult = heapAnalyzer.findLeakTrace(0, snapshot, instanceWrapper.instance);
            if (analysisResult != null) {
                instanceWrapper.referenceChain = analysisResult;
                instanceWrapper.found = analysisResult.leakFound;
                instanceWrapper.classObj = instanceWrapper.instance.getClassObj();
                instanceWrapper.leakTrace = analysisResult.leakTrace;
                instanceWrapper.retainedHeapSize = analysisResult.retainedHeapSize;
                instanceWrapper.sizeRation = analysisResult.retainedHeapSize*1.0/totalRetainedSize;
                instanceWrapper.id = instanceWrapper.instance.getId();
            }
        }
    }

    public StableList<InstanceWrapper> getTopInstanceList() {
        return topInstanceList;
    }

    private StableList<InstanceWrapper> initTopInstance() {
        topInstanceList = new StableList<>();
        List<Instance> instanceList = snapshot.getReachableInstances();
        for (Instance instance : instanceList) {
            totalRetainedSize += instance.getSize();
            if (instance.getClassObj() == null || instance.getClassObj().getClassName().contains("$")) {
                continue;
            }
            InstanceWrapper instanceWrapper = new InstanceWrapper(instance);
            instanceWrapper.retainedHeapSize = instance.getTotalRetainedSize();
            topInstanceList.add(instanceWrapper);
        }
        return topInstanceList;
    }

}
