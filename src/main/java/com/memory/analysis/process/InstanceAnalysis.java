package com.memory.analysis.process;

import com.memory.analysis.db.InstanceDBDao;
import com.memory.analysis.entity.InstanceResultEntity;
import com.memory.analysis.leak.AnalysisResult;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.utils.Constants;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;

import java.util.List;

public class InstanceAnalysis {

    public long totalRetainedSize;

    public StableList<InstanceWrapper> topInstanceList;

    private StableList<InstanceWrapper> topActivityClassList;

    private Snapshot snapshot;
    private final HeapAnalyzer heapAnalyzer;

    private InstanceDBDao instanceDBDao;
    private String hprofFileName;

    public InstanceAnalysis(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String hprofFileName) {
        this.snapshot = snapshot;
        this.heapAnalyzer = heapAnalyzer;
        this.hprofFileName = hprofFileName;
        instanceDBDao = new InstanceDBDao();
        topInstanceList = initTopInstance();
        findRefeference(topInstanceList);
    }

    private void findRefeference(StableList<InstanceWrapper> topInstanceList) {
        for (InstanceWrapper instanceWrapper : topInstanceList) {
            AnalysisResult analysisResult = heapAnalyzer.findLeakTrace(0, snapshot, instanceWrapper.instance);
            if (analysisResult != null) {
                instanceWrapper.fill(analysisResult, totalRetainedSize);
                if (instanceWrapper.found) {
                    InstanceResultEntity instanceResultEntity = instanceDBDao.query(instanceWrapper.classObj.getClassName());
                    if (instanceResultEntity != null) {
                        instanceDBDao.update(instanceResultEntity, hprofFileName, instanceWrapper.retainedHeapSize / 1024.0 / 1024.0, instanceWrapper
                                .leakTrace.toString());
                    } else {
                        instanceDBDao.add(instanceWrapper.classObj.getClassName(), 1, instanceWrapper.retainedHeapSize / 1024.0 / 1024.0, hprofFileName,
                                instanceWrapper.leakTrace.toString());
                    }
                    System.out.println(instanceWrapper);
                }
            }
        }
        instanceDBDao.close();
        for (InstanceWrapper instanceWrapper : topActivityClassList) {
            System.out.println("class name is " + instanceWrapper.instance.getClassObj().getClassName().toLowerCase());
            AnalysisResult analysisResult = heapAnalyzer.findLeakTrace(0, snapshot, instanceWrapper.instance);
            if (analysisResult != null) {
                instanceWrapper.fill(analysisResult, totalRetainedSize);
            }
            System.out.println("topActivityClassList size  is " + topActivityClassList.size());
        }
    }

    public StableList<InstanceWrapper> getTopInstanceList() {
        return topInstanceList;
    }

    public StableList<InstanceWrapper> getTopActivityClassList() {
        return topActivityClassList;
    }

    private StableList<InstanceWrapper> initTopInstance() {
        topInstanceList = new StableList<>();
        topActivityClassList = new StableList<>();
        List<Instance> instanceList = snapshot.getReachableInstances();
        for (Instance instance : instanceList) {
            totalRetainedSize += instance.getSize();
            if (!(instance instanceof ClassInstance) || instance.getClassObj() == null || instance.getClassObj().getClassName().contains("$")) {
                continue;
            }
            InstanceWrapper instanceWrapper = new InstanceWrapper(instance);
            instanceWrapper.retainedHeapSize = instance.getTotalRetainedSize();
            topInstanceList.add(instanceWrapper);
            if (instanceWrapper.instance.getClassObj().getClassName().endsWith(Constants.ANDROID_BACTIVITY_CLASS)) {
                topActivityClassList.add(instanceWrapper);
            }
        }
        return topInstanceList;
    }

}
