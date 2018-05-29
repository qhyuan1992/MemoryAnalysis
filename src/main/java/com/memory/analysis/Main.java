package com.memory.analysis;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.exclusion.ExcludedRefs;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.process.ClassAnalysis;
import com.memory.analysis.process.ClassObjWrapper;
import com.memory.analysis.process.InstanceAnalysis;
import com.memory.analysis.process.InstanceWrapper;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.HprofParser;
import com.squareup.haha.perflib.Snapshot;
import com.squareup.haha.perflib.io.HprofBuffer;
import com.squareup.haha.perflib.io.MemoryMappedFileBuffer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by weiersyuan on 2018/5/12.
 */
public class Main {
    public static final String hprofFilePath = "src/main/resources/test2.hprof";
    public static final String instanceOutFilePath = "src/main/resources/instance.txt";
    public static final String classOutFilePath = "src/main/resources/class.txt";
    public static final String activityOutFilePath = "src/main/resources/avtivity.txt";
    public static String dirPath = "src/main/resources/";

    public static void main(String [] args) throws IOException {
        /**
         * 遍历当前目录下的所有hprof文件
         */
        File file = new File(dirPath);
        File[] array = file.listFiles();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile() && array[i].getName().endsWith("hprof")) {
                File hprofFile = new File(dirPath + array[i].getName());
                handleHprof(hprofFile);
            }
        }
    }

    private static void handleHprof(File hprofFile) throws IOException {
        System.out.println("handle file " + hprofFile.getPath());
        final HprofBuffer buffer = new MemoryMappedFileBuffer(hprofFile);
        final HprofParser parser = new HprofParser(buffer);
        final Snapshot snapshot = parser.parse();
        snapshot.computeDominators();
        ExcludedRefs refs = AndroidExcludedRefs.createAppDefaults().build();
        HeapAnalyzer heapAnalyzer = new HeapAnalyzer(refs);

        // 分析所有的实例
        Thread instanceThread = new Thread(new InstanceRunnable(snapshot, heapAnalyzer, instanceOutFilePath,hprofFile.getName(),activityOutFilePath));
        // 分析所有的类
        Thread classThread = new Thread(new ClassRunnable(snapshot, heapAnalyzer, classOutFilePath));

        instanceThread.start();
        classThread.start();
    }

    static class InstanceRunnable implements Runnable{
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;
        String hprofFileName;
        File activityClassOutFile;

        InstanceRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String pathName , String hprofFileName,String activityClassOutFilePath) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(pathName);
            this.hprofFileName = hprofFileName;
            this.activityClassOutFile = new File(activityClassOutFilePath);
        }

        @Override
        public void run() {
            InstanceAnalysis instanceAnalysis = new InstanceAnalysis(snapshot, heapAnalyzer,hprofFileName);
            StableList<InstanceWrapper> topInstanceList = instanceAnalysis.getTopInstanceList();
            StableList<InstanceWrapper> topActivityClassList = instanceAnalysis.getTopActivityClassList();
            try {
                FileUtils.writeLines(file, topInstanceList, true);
                FileUtils.writeLines(activityClassOutFile, topActivityClassList, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClassRunnable implements Runnable{
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;

        ClassRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String classOutFilePath) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(classOutFilePath);
        }

        @Override
        public void run() {
            ClassAnalysis classAnalysis = new ClassAnalysis(snapshot, heapAnalyzer);
            StableList<ClassObjWrapper> topClassList = classAnalysis.getTopInstanceList();
            try {
                FileUtils.writeLines(file, topClassList);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
