package com.memory.analysis;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.db.InstanceResultDao;
import com.memory.analysis.exclusion.ExcludedRefs;
import com.memory.analysis.leak.HeapAnalyzer;
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
import java.util.concurrent.Callable;

/**
 * @author cainjiang
 * @date 2018/7/5
 */
public class InstanceCallable implements Callable<Integer> {
    File file;
    File activityClassOutFile;
    File hprofFile;
    HprofBuffer hprofBuffer;
    HprofParser hprofParser;
    InstanceResultDao instanceResultMySqlDao;

    public InstanceCallable(File hprofFile, String pathName, String activityClassOutFilePath, InstanceResultDao instanceResultMySqlDao) throws IOException {
        this.file = new File(pathName);
        this.activityClassOutFile = new File(activityClassOutFilePath);
        this.hprofFile = hprofFile;
        this.instanceResultMySqlDao = instanceResultMySqlDao;
        this.hprofBuffer = new MemoryMappedFileBuffer(hprofFile);
        this.hprofParser = new HprofParser(hprofBuffer);
    }

    public File getHprofFile() {
        return hprofFile;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("begin parse instance and activity in file " + hprofFile.getPath());
        long start = System.currentTimeMillis();
        Snapshot snapshot = hprofParser.parse();
        snapshot.computeDominators();
        ExcludedRefs refs = AndroidExcludedRefs.createAppDefaults().build();
        InstanceAnalysis instanceAnalysis = new InstanceAnalysis(snapshot, new HeapAnalyzer(refs));
        StableList<InstanceWrapper> topInstanceList = instanceAnalysis.getTopInstanceList();
        StableList<InstanceWrapper> topActivityClassList = instanceAnalysis.getTopActivityClassList();
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
        return Constant.PROCESS_RESULT_OK;
    }
}
