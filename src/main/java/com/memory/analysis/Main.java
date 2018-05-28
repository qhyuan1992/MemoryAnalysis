package com.memory.analysis;

import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.process.ClassAnalysis;
import com.memory.analysis.process.ClassObjWrapper;
import com.memory.analysis.process.InstanceAnalysis;
import com.memory.analysis.process.InstanceWrapper;
import com.memory.analysis.utils.Constants;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.HprofParser;
import com.squareup.haha.perflib.Instance;
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
    // /Users/weiersyuan/Desktop/test2.hprof
    // /Users/weiersyuan/Desktop/123/dump_LowMemory.hprof
    public static final String hprofFilePath = "/Users/weiersyuan/Desktop/123/dump_LowMemory.hprof";
    public static final String instanceOutFilePath = "/Users/weiersyuan/Desktop/test/out/instance.txt";
    public static final String classOutFilePath = "/Users/weiersyuan/Desktop/test/out/class.txt";
    public static final String activityOutFilePath = "/Users/weiersyuan/Desktop/test/out/avtivity.txt";

    public static void main(String [] args) throws IOException {
        final File hprofFile = new File(hprofFilePath);
        final HprofBuffer buffer = new MemoryMappedFileBuffer(hprofFile);
        final HprofParser parser = new HprofParser(buffer);
        final Snapshot snapshot = parser.parse();
        snapshot.computeDominators();
        HeapAnalyzer heapAnalyzer = new HeapAnalyzer();

        // 分析所有的实例
        Thread instanceThread = new Thread(new InstanceRunnable(snapshot, heapAnalyzer, instanceOutFilePath));
        // 分析所有的类
        Thread classThread = new Thread(new ClassRunnable(snapshot, heapAnalyzer, classOutFilePath, activityOutFilePath));

        instanceThread.start();
        classThread.start();
    }

    private static void findMayActivityLeak(Snapshot snapshot) {
        ClassObj activityClassObj = snapshot.findClass(Constants.ANDROID_BACTIVITY_CLASS);

        for (Instance instance : activityClassObj.getInstancesList()) {
            System.out.println(instance.getClassObj().getClassName());
        }
    }

    static class InstanceRunnable implements Runnable{
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;

        InstanceRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String pathName) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(pathName);
        }

        @Override
        public void run() {
            InstanceAnalysis instanceAnalysis = new InstanceAnalysis(snapshot, heapAnalyzer);
            StableList<InstanceWrapper> topInstanceList = instanceAnalysis.getTopInstanceList();
            try {
                FileUtils.writeLines(file, topInstanceList, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClassRunnable implements Runnable{
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;
        File activityClassOutFile;

        ClassRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String classOutFilePath, String activityClassOutFilePath) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(classOutFilePath);
            this.activityClassOutFile = new File(activityClassOutFilePath);
        }

        @Override
        public void run() {
            ClassAnalysis classAnalysis = new ClassAnalysis(snapshot, heapAnalyzer);
            StableList<ClassObjWrapper> topClassList = classAnalysis.getTopInstanceList();
            StableList<ClassObjWrapper> topActivityClassList = classAnalysis.getTopActivityClassList();

            try {
                FileUtils.writeLines(file, topClassList);
                FileUtils.writeLines(activityClassOutFile, topActivityClassList);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }





    public void test() {

//        snapshot.dumpInstanceCounts();
//        snapshot.dumpSizes();
//        snapshot.getGCRoots();

//        snapshot.computeDominators();
//            List<Instance> instanceList = snapshot.getReachableInstances();
//        for (Instance instance : instanceList) {
////            System.out.println(Long.toHexString(instance.getId()));
//        }
//        Instance i = snapshot.findInstance(0x1315bf10);
//        System.out.println(i.getTotalRetainedSize());
//        System.out.println(i.getSize());


        /**
         * 0x13539d60
         * android.graphics.Bitmapleak 0.8898191452026367M
         * GC ROOT static com.facebook.common.references.SharedReference.sLiveObjects
         * references java.util.IdentityHashMap.table
         * references array java.lang.Object[].[578]
         * leaks android.graphics.Bitmap instance
         *
         *
         * 0x1315bf10
         * android.graphics.Bitmapleak 1.1574506759643555M
         * GC ROOT thread android.os.HandlerThread.<Java Local> (named 'DFM Handler Thread #0')
         * references master.flame.danmaku.controller.DrawHandler.mDanmakuView
         * references master.flame.danmaku.ui.widget.DanmakuView.mResources
         * references android.content.res.Resources.mResourcesImpl
         * references android.content.res.ResourcesImpl.mDrawableCache
         * references android.content.res.DrawableCache.mUnthemedEntries
         * references android.util.LongSparseArray.mValues
         * references array java.lang.Object[].[131]
         * references java.lang.ref.WeakReference.referent
         * references android.graphics.drawable.BitmapDrawable$BitmapState.mBitmap
         * leaks android.graphics.Bitmap instance
         *
         */

        // 0x13539d60    0x1315bf10
        /*Instance i = snapshot.findInstance(0x1315bf10);//0x1315bf10
        HeapAnalyzer heapAnalyzer = new HeapAnalyzer();
        AnalysisResult result = heapAnalyzer.findLeakTrace(0, snapshot, i);
        System.out.println(result.className + "leak " + result.retainedHeapSize /1024.0/1024.0 + "M");
        System.out.println(result.leakTrace.toString());*/

    }
}
