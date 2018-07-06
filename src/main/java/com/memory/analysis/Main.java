package com.memory.analysis;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.db.HandleResultDao;
import com.memory.analysis.db.InstanceResultDao;
import com.memory.analysis.db.factory.IFactory;
import com.memory.analysis.db.factory.MySqlFactory;
import com.memory.analysis.entity.HandleResultEntity;
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
import java.util.concurrent.*;

/**
 * Created by weiersyuan on 2018/5/12.
 */
public class Main {

    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_CLASS = 1;
    public static final int TYPE_INSTANCE = 2;

    public static final String instanceOutFilePathPatten = "src/main/resources/%s/%s_instance.txt";
    public static final String classOutFilePathPatten = "src/main/resources/%s/%s_class.txt";
    public static final String activityOutFilePathPatten = "src/main/resources/%s/%s_activity.txt";
    public static String dirPath = "src/main/files/";
    // 处理HPROF文件超时(min)
    public static final int TIME_OUT = 5;

    private static ExecutorService mSingleThreadPool;
    private static InstanceResultDao instanceResultMySqlDao;
    private static ClassResultDao classResultMySqlDao;
    private static HandleResultDao mHandleResultDao;

    private static int sumHandleHprof = 0;
    private static int successNum = 0;


    public static String generateFilepath(int type, String fileName) {
        String result = null;
        switch (type) {
            case TYPE_ACTIVITY:
                result = String.format(activityOutFilePathPatten, fileName, fileName);
                break;
            case TYPE_CLASS:
                result = String.format(classOutFilePathPatten, fileName, fileName);
                break;
            case TYPE_INSTANCE:
                result = String.format(instanceOutFilePathPatten, fileName, fileName);
                break;
        }
        return result;
    }

    public static String getFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    public static void main(String[] args) throws IOException {
        IFactory iFactory = new MySqlFactory();
        instanceResultMySqlDao = iFactory.createInstanceResultDao();
        classResultMySqlDao = iFactory.createClassResultDao();

        mHandleResultDao = iFactory.createHandleResultDao();
        ConnectionUtil connectionUtil = new ConnectionUtil();
        Connection connection = connectionUtil.getConnection();
        mHandleResultDao.setConn(connection,Thread.currentThread().getId());

        mSingleThreadPool = Executors.newSingleThreadExecutor();
        long startTime = System.currentTimeMillis();
        /**
         * 遍历当前目录下的所有hprof文件
         */
        File dirFiles = new File(dirPath);
        File[] arrayFiles = dirFiles.listFiles();
        for (int i = 0; i < arrayFiles.length; i++) {
            if (arrayFiles[i].isFile() && arrayFiles[i].getName().endsWith("hprof")) {
                sumHandleHprof++;
                File hprofFile = new File(dirPath + arrayFiles[i].getName());

                Callable<Integer> instanceCallable = new InstanceCallable(hprofFile, generateFilepath(TYPE_INSTANCE, getFileName(hprofFile)), generateFilepath(TYPE_ACTIVITY, getFileName(hprofFile)), instanceResultMySqlDao);
                FutureTask<Integer> futureTask = new FutureTask<>(instanceCallable);
                int instanceHandleResult = handleHprofFile(TIME_OUT, futureTask, hprofFile, 1);
                if (instanceHandleResult != Constant.PROCESS_RESULT_OK) {
                    // todo 记录处理失败的hprof文件
                }

                Callable<Integer> classCallable = new ClassCallable(hprofFile, generateFilepath(TYPE_CLASS, getFileName(hprofFile)), classResultMySqlDao);
                futureTask = new FutureTask<>(classCallable);
                int classHandleResult = handleHprofFile(TIME_OUT, futureTask, hprofFile, 1);
                if (classHandleResult != Constant.PROCESS_RESULT_OK) {
                    // todo 记录处理失败的hprof文件
                }

                if (instanceHandleResult == Constant.PROCESS_RESULT_OK && classHandleResult == Constant.PROCESS_RESULT_OK) {
                    successNum++;
                }
            }
        }

        mSingleThreadPool.shutdown();
        long endTime = System.currentTimeMillis();
        System.out.println("处理结束，总共处理了" + sumHandleHprof + "份hprof文件，成功" + successNum + "次，耗时" + (endTime - startTime) / 1000 + " s");

    }

    /**
     *
     * @param timeout
     * @param futureTask
     * @param hprofFile
     * @param time
     * @return
     */
    private static int handleHprofFile(int timeout, FutureTask<Integer> futureTask, File hprofFile, int time) {
        if (time > 2) {
            return Constant.PROCESS_RESULT_FAIL;
        }
        mSingleThreadPool.execute(futureTask);
        int processResult = Constant.PROCESS_RESULT_DEFAULT;
        try {
            processResult = futureTask.get(timeout, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            futureTask.cancel(true);
            processResult = Constant.PROCESS_RESULT_FAIL_INTERRRUPTED;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time interrupted!");
            e.printStackTrace();
        } catch (ExecutionException e) {
            futureTask.cancel(true);
            processResult = Constant.PROCESS_RESULT_FAIL_EXECUTION;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time execute failed!");
            e.printStackTrace();
        } catch (TimeoutException e) {
            futureTask.cancel(true);
            processResult = Constant.PROCESS_RESULT_FAIL_TIMEOUT;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time Timeout!");
            e.printStackTrace();
        }
        if (processResult == Constant.PROCESS_RESULT_OK) {
            return processResult;
        } else {
            // 如果失败，迭代再处理一次，超时时间扩大一倍
            processResult = handleHprofFile(TIME_OUT * (time + 1), futureTask, hprofFile, time + 1);
        }
        return processResult;
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
