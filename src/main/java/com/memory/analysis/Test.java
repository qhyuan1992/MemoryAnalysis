package com.memory.analysis;

import com.memory.analysis.leak.AnalysisResult;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.HprofParser;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;
import com.squareup.haha.perflib.io.HprofBuffer;
import com.squareup.haha.perflib.io.MemoryMappedFileBuffer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by weiersyuan on 2018/5/12.
 */
public class Test {
    public static void main(String [] args) throws IOException {
        final File hprofFile = new File("/Users/weiersyuan/Desktop/test2.hprof");
        final HprofBuffer buffer = new MemoryMappedFileBuffer(hprofFile);
        final HprofParser parser = new HprofParser(buffer);
        final Snapshot snapshot = parser.parse();
        snapshot.computeDominators();

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


        HeapAnalyzer heapAnalyzer = new HeapAnalyzer();
        StableList list = getAllInstance(snapshot);
        for (int i = 0; i < list.size(); i++) {
            Instance instance = list.get(i);
            System.out.println("------>" + instance);
            if (instance instanceof ClassInstance) {
                AnalysisResult result = heapAnalyzer.findLeakTrace(0, snapshot, instance);
                System.out.println(result.className + " leak " + result.retainedHeapSize /1024.0/1024.0 + "M");
                System.out.println(result.leakTrace.toString());
            }
        }

    }

    private static StableList getAllInstance(Snapshot snapshot) {
        StableList list = new StableList();
        List<Instance> instanceList = snapshot.getReachableInstances();
        for (Instance instance : instanceList) {
            list.add(instance);
        }
        return list;
    }
}
