package com.memory.analysis;

import com.memory.analysis.db.HandleResultDao;
import com.memory.analysis.db.InstanceResultDao;
import com.memory.analysis.db.factory.IFactory;
import com.memory.analysis.db.factory.MySqlFactory;
import com.memory.analysis.entity.HandleResultEntity;
import com.memory.analysis.entity.InstanceResultEntity;
import com.memory.analysis.process.ClassCallable;
import com.memory.analysis.process.InstanceCallable;
import com.memory.analysis.utils.CheckerUtil;
import com.memory.analysis.utils.ConnectionUtil;
import com.memory.analysis.utils.Constants;
import com.memory.analysis.utils.FormatUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


/**
 * Created by weiersyuan on 2018/5/12.
 */
public class Main {
    private static ExecutorService mSingleThreadPool;
    private static InstanceResultDao mInstanceResultMySqlDao;
    private static HandleResultDao<HandleResultEntity> mHandleResultDao;

    private static int sumNum = 0;
    private static int successNum = 0;

    public static void main(String[] args) {
        IFactory factory = new MySqlFactory();

        mInstanceResultMySqlDao = factory.createInstanceResultDao();
        mInstanceResultMySqlDao.setConn(new ConnectionUtil().getConnection(), Thread.currentThread().getId());

        mHandleResultDao = factory.createHandleResultDao();
        mHandleResultDao.setConn(new ConnectionUtil().getConnection(), Thread.currentThread().getId());

        mSingleThreadPool = Executors.newSingleThreadExecutor();
        long startTime = System.currentTimeMillis();
        /**
         * 遍历当前目录下的所有hprof文件
         */
        File dirFiles = new File(Constants.DIRPATH);
        File[] arrayFiles = dirFiles.listFiles();

        try {
            for (int i = 0; i < arrayFiles.length; i++) {
                if (arrayFiles[i].isFile() && arrayFiles[i].getName().endsWith("hprof")) {
                    sumNum++;
                    File hprofFile = new File(Constants.DIRPATH + arrayFiles[i].getName());

                    Callable<Integer> instanceCallable = new InstanceCallable(hprofFile, FormatUtil.generateFilepath(Constants.TYPE_INSTANCE, FormatUtil.getFileName(hprofFile)), FormatUtil.generateFilepath(Constants.TYPE_ACTIVITY, FormatUtil.getFileName(hprofFile)), factory);
                    FutureTask<Integer> futureTask = new FutureTask<>(instanceCallable);
                    int instanceHandleResult = handleHprofFile(Constants.TIME_OUT, futureTask, hprofFile, 1);
                    if (instanceHandleResult != Constants.PROCESS_RESULT_OK) {
                        // 将没有成功处理的结果记录下来
                        mHandleResultDao.add(new HandleResultEntity(hprofFile.getName(), Constants.HANDLE_TYPE_INSTANCE, Constants.HANDLE_STATUS_FAIL));
                    }

                    Callable<Integer> classCallable = new ClassCallable(hprofFile, FormatUtil.generateFilepath(Constants.TYPE_CLASS, FormatUtil.getFileName(hprofFile)), factory);
                    futureTask = new FutureTask<>(classCallable);
                    int classHandleResult = handleHprofFile(Constants.TIME_OUT, futureTask, hprofFile, 1);
                    if (classHandleResult != Constants.PROCESS_RESULT_OK) {
                        // 将没有成功处理的结果记录下来
                        mHandleResultDao.add(new HandleResultEntity(hprofFile.getName(), Constants.HANDLE_TYPE_CLASS, Constants.HANDLE_STATUS_FAIL));
                    }

                    if (instanceHandleResult == Constants.PROCESS_RESULT_OK && classHandleResult == Constants.PROCESS_RESULT_OK) {
                        successNum++;
                    }
                }
            }
            mHandleResultDao.getConn(Thread.currentThread().getId()).close();
            mSingleThreadPool.shutdown();

            // 生成结果报表
            StringBuilder sql = new StringBuilder("SELECT * FROM ").
                    append(Constants.INSTANCE_RESULT_TABLE).
                    append(" ORDER BY ").
                    append(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK).
                    append(" ").
                    append("LIMIT ").
                    append(Constants.TOP);
            List<InstanceResultEntity> instanceResultEntityList = mInstanceResultMySqlDao.query(sql.toString());
            mInstanceResultMySqlDao.getConn(Thread.currentThread().getId()).close();
            if (!CheckerUtil.isEmpty(instanceResultEntityList)) {
                System.out.println(instanceResultEntityList.toString());
                StringBuilder tips = new StringBuilder("parse ").
                        append(sumNum).
                        append(" hprof files in all, and ").
                        append(successNum).
                        append(" files are parsed successfully.").
                        append("\n").
                        append("--------------------").
                        append("the most ").
                        append(Constants.TOP).
                        append(" retained memory--------------------").
                        append("\n");
                // 用当前时间作为文件名
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String filePath = FormatUtil.generateFilepath(Constants.TYPE_REPORT, simpleDateFormat.format(new Date()));
                File parseResultFile = new File(filePath);
                FileUtils.writeStringToFile(parseResultFile, tips.toString(), "utf-8");
                FileUtils.writeLines(parseResultFile, "utf-8", instanceResultEntityList, true);
            }

        } catch (IOException e) {
            e.getStackTrace();
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            e.getStackTrace();
            System.out.println(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("处理结束，总共处理了" + sumNum + "份hprof文件，成功" + successNum + "份，耗时" + (endTime - startTime) / 1000 + " s");

    }

    /**
     * @param timeout
     * @param futureTask
     * @param hprofFile
     * @param time
     * @return
     */
    private static int handleHprofFile(int timeout, FutureTask<Integer> futureTask, File hprofFile, int time) {
        if (time > 2) {
            return Constants.PROCESS_RESULT_FAIL;
        }
        mSingleThreadPool.execute(futureTask);
        int processResult = Constants.PROCESS_RESULT_DEFAULT;
        try {
            processResult = futureTask.get(timeout, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // 没有处理成功，先关闭任务
            futureTask.cancel(true);
            processResult = Constants.PROCESS_RESULT_FAIL_INTERRRUPTED;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time interrupted!");
            e.printStackTrace();
        } catch (ExecutionException e) {
            // 没有处理成功，先关闭任务
            futureTask.cancel(true);
            processResult = Constants.PROCESS_RESULT_FAIL_EXECUTION;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time execute failed!");
            e.printStackTrace();
        } catch (TimeoutException e) {
            // 没有处理成功，先关闭任务
            futureTask.cancel(true);
            processResult = Constants.PROCESS_RESULT_FAIL_TIMEOUT;
            System.out.println("handle " + hprofFile.getName() + " " + time + " time Timeout!");
            e.printStackTrace();
        }
        if (processResult == Constants.PROCESS_RESULT_OK) {
            futureTask.cancel(true);
            return processResult;
        } else {
            // 如果失败，迭代再处理一次，超时时间扩大一倍
            processResult = handleHprofFile(Constants.TIME_OUT * (time + 1), futureTask, hprofFile, time + 1);
        }
        return processResult;
    }

}
