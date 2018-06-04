package com.memory.analysis;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.db.InstanceResultDao;
import com.memory.analysis.db.factory.IFactory;
import com.memory.analysis.db.factory.MySqlFactory;
import com.memory.analysis.exclusion.ExcludedRefs;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.process.ClassAnalysis;
import com.memory.analysis.process.ClassObjWrapper;
import com.memory.analysis.process.InstanceAnalysis;
import com.memory.analysis.process.InstanceWrapper;
import com.memory.analysis.utils.CheckerUtil;
import com.memory.analysis.utils.ConnectionUtil;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.HprofParser;
import com.squareup.haha.perflib.Snapshot;
import com.squareup.haha.perflib.io.HprofBuffer;
import com.squareup.haha.perflib.io.MemoryMappedFileBuffer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by weiersyuan on 2018/5/12.
 */
public class Main {
    public static final String hprofFilePath = "src/main/resources/test2.hprof";
    public static final String instanceOutFilePath = "src/main/resources/instance.txt";
    public static final String classOutFilePath = "src/main/resources/class.txt";
    public static final String activityOutFilePath = "src/main/resources/activity.txt";
    public static String dirPath = "src/main/files/";
    // 处理HPROF文件超时(ms)
    public static final int TIME_OUT = 20 * 1000;

    static ExecutorService fixedThreadPool;
    static InstanceResultDao instanceResultMySqlDao;
    static ClassResultDao classResultMySqlDao;


    public static void main(String[] args) throws IOException {
        IFactory iFactory = new MySqlFactory();
        instanceResultMySqlDao = iFactory.createInstanceResultDao();
        classResultMySqlDao = iFactory.createClassResultDao();
        fixedThreadPool = Executors.newFixedThreadPool(10);
        /**
         * 遍历当前目录下的所有hprof文件
         */
        File dirFiles = new File(dirPath);
        File[] arrayFiles = dirFiles.listFiles();
        for (int i = 0; i < arrayFiles.length; i++) {
            if (arrayFiles[i].isFile() && arrayFiles[i].getName().endsWith("hprof")) {
                File hprofFile = new File(dirPath + arrayFiles[i].getName());
                fixedThreadPool.execute(new InstanceRunnable(hprofFile, instanceOutFilePath, activityOutFilePath));
                fixedThreadPool.execute(new ClassRunnable(hprofFile, classOutFilePath));
            }
        }
    }

    static class InstanceRunnable implements Runnable {
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;
        String hprofFileName;
        File activityClassOutFile;
        HprofBuffer hprofBuffer;
        HprofParser hprofParser;
        File hprofFile;

        InstanceRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String pathName, String hprofFileName, String activityClassOutFilePath) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(pathName);
            this.hprofFileName = hprofFileName;
            this.activityClassOutFile = new File(activityClassOutFilePath);
        }

        InstanceRunnable(File hprofFile, String pathName, String activityClassOutFilePath) throws IOException {
            this.file = new File(pathName);
            this.activityClassOutFile = new File(activityClassOutFilePath);
            this.hprofFile = hprofFile;
            hprofBuffer = new MemoryMappedFileBuffer(hprofFile);
            hprofParser = new HprofParser(hprofBuffer);
        }

        @Override
        public void run() {
            System.out.println("begin parse instance and activity in file " + hprofFile.getPath());
            long start = System.currentTimeMillis();
            Snapshot snapshot = hprofParser.parse();
            snapshot.computeDominators();
            ExcludedRefs refs = AndroidExcludedRefs.createAppDefaults().build();
            InstanceAnalysis instanceAnalysis = new InstanceAnalysis(snapshot, new HeapAnalyzer(refs));
            StableList<InstanceWrapper> topInstanceList = instanceAnalysis.getTopInstanceList();
            StableList<InstanceWrapper> topActivityClassList = instanceAnalysis.getTopActivityClassList();
            try {
                FileUtils.writeLines(file, topInstanceList, true);

                ConnectionUtil connectionUtil = new ConnectionUtil();
                Connection connection = connectionUtil.getConnection();
                instanceResultMySqlDao.setConn(connection, Thread.currentThread().getId());
                instanceResultMySqlDao.setTableName("instance_result_table");
                for (InstanceWrapper instanceWrapper : topInstanceList) {
                    if (!CheckerUtil.isEmpty(instanceWrapper)) {
                        instanceResultMySqlDao.handle(instanceWrapper, hprofFile.getName());
                    }
                }

                FileUtils.writeLines(activityClassOutFile, topActivityClassList, true);

                instanceResultMySqlDao.setTableName("activity_result_table");
                for (InstanceWrapper instanceWrapper : topActivityClassList) {
                    if (!CheckerUtil.isEmpty(instanceWrapper)) {
                        instanceResultMySqlDao.handle(instanceWrapper, hprofFile.getName());
                    }
                }

                instanceResultMySqlDao.getConn(Thread.currentThread().getId()).close();
                System.out.println("finish parse instance and activity in file" + hprofFile.getPath() + " in " + (System.currentTimeMillis() - start) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    static class ClassRunnable implements Runnable {
        Snapshot snapshot;
        HeapAnalyzer heapAnalyzer;
        File file;
        String hprofFileName;
        HprofBuffer hprofBuffer;
        HprofParser hprofParser;
        File hprofFile;

        ClassRunnable(Snapshot snapshot, HeapAnalyzer heapAnalyzer, String classOutFilePath, String hprofFileName) {
            this.snapshot = snapshot;
            this.heapAnalyzer = heapAnalyzer;
            this.file = new File(classOutFilePath);
            this.hprofFileName = hprofFileName;
        }

        ClassRunnable(File hprofFile, String classOutFilePath) throws IOException {
            this.file = new File(classOutFilePath);
            this.hprofFile = hprofFile;
            hprofBuffer = new MemoryMappedFileBuffer(hprofFile);
            hprofParser = new HprofParser(hprofBuffer);
        }

        @Override
        public void run() {
            System.out.println("begin parse class in file " + hprofFile.getPath());
            long start = System.currentTimeMillis();
            Snapshot snapshot = hprofParser.parse();
            snapshot.computeDominators();
            ExcludedRefs refs = AndroidExcludedRefs.createAppDefaults().build();
            ClassAnalysis classAnalysis = new ClassAnalysis(snapshot, new HeapAnalyzer(refs));
            StableList<ClassObjWrapper> topClassList = classAnalysis.getTopInstanceList();
            try {
                FileUtils.writeLines(file, topClassList);

                ConnectionUtil connectionUtil = new ConnectionUtil();
                Connection connection = connectionUtil.getConnection();
                classResultMySqlDao.setConn(connection, Thread.currentThread().getId());
                classResultMySqlDao.setTableName("class_result_table");
                for (ClassObjWrapper classObjectWrapper : topClassList) {
                    classResultMySqlDao.handle(classObjectWrapper, hprofFile.getName());
                }
                classResultMySqlDao.getConn(Thread.currentThread().getId()).close();
                System.out.println("finish parse class in file " + hprofFile.getPath() + " in " + (System.currentTimeMillis() - start) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

}
