package com.memory.analysis;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.exclusion.ExcludedRefs;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.process.ClassAnalysis;
import com.memory.analysis.process.ClassObjWrapper;
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
public class ClassCallable implements Callable<Integer> {
    File file;
    File hprofFile;
    HprofBuffer hprofBuffer;
    HprofParser hprofParser;
    ClassResultDao classResultMySqlDao;

    public ClassCallable(File hprofFile, String classOutFilePath,ClassResultDao classResultMySqlDao) throws IOException {
        this.file = new File(classOutFilePath);
        this.hprofFile = hprofFile;
        this.classResultMySqlDao = classResultMySqlDao;
        this.hprofBuffer = new MemoryMappedFileBuffer(hprofFile);
        this.hprofParser = new HprofParser(hprofBuffer);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("begin parse class in file " + hprofFile.getPath());
        long start = System.currentTimeMillis();
        Snapshot snapshot = hprofParser.parse();
        snapshot.computeDominators();
        ExcludedRefs refs = AndroidExcludedRefs.createAppDefaults().build();
        ClassAnalysis classAnalysis = new ClassAnalysis(snapshot, new HeapAnalyzer(refs));
        StableList<ClassObjWrapper> topClassList = classAnalysis.getTopInstanceList();
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
        return Constant.PROCESS_RESULT_OK;
    }
}
