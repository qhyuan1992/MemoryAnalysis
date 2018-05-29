package com.memory.analysis.process;

import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.Heap;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;

import java.util.Collection;

public class ClassAnalysis {
    private Snapshot snapshot;
    private final HeapAnalyzer heapAnalyzer;
    private StableList<ClassObjWrapper> topClassList;

    public ClassAnalysis(Snapshot snapshot, HeapAnalyzer heapAnalyzer) {
        this.snapshot = snapshot;
        this.heapAnalyzer = heapAnalyzer;
        initTopClassList();
    }

    public StableList<ClassObjWrapper> getTopInstanceList() {
        return topClassList;
    }

    private void initTopClassList() {
        topClassList = new StableList<>();
        Collection<Heap> allHeaps =  snapshot.getHeaps();
        for (Heap heap: allHeaps) {
            Collection<ClassObj> classObjs = heap.getClasses();
            for (ClassObj classObj : classObjs) {
                if (classObj == null || classObj.getClassName().contains("$")) {
                    continue;
                }
                ClassObjWrapper classObjWrapper = new ClassObjWrapper(classObj);
                classObjWrapper.instanceCount = classObj.getInstanceCount();
                for (Instance instance : classObj.getInstancesList()) {
                    classObjWrapper.retainedHeapSize += instance.getTotalRetainedSize();
                }
                topClassList.add(classObjWrapper);
            }
        }
    }
}
