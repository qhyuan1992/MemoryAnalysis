package com.memory.analysis.process;

import com.memory.analysis.android.AndroidExcludedRefs;
import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.db.factory.IFactory;
import com.memory.analysis.exclusion.ExcludedRefs;
import com.memory.analysis.leak.HeapAnalyzer;
import com.memory.analysis.utils.ConnectionUtil;
import com.memory.analysis.utils.Constants;
import com.memory.analysis.utils.StableList;
import com.squareup.haha.perflib.HprofParser;
import com.squareup.haha.perflib.Snapshot;
import com.squareup.haha.perflib.io.HprofBuffer;
import com.squareup.haha.perflib.io.MemoryMappedFileBuffer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

/**
 * @author cainjiang
 * @date 2018/7/5
 */
public class ClassCallable extends BaseCallable{

    File file;
    File hprofFile;
    HprofBuffer hprofBuffer;
    HprofParser hprofParser;
    ClassResultDao classResultMySqlDao;

    public ClassCallable(File hprofFile, String classOutFilePath,IFactory factory) throws IOException {
        super(factory);
        this.file = new File(classOutFilePath);
        this.hprofFile = hprofFile;
        this.classResultMySqlDao = mFactory.createClassResultDao();
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
        System.out.println("topClassList:\n" + topClassList.toString());
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
        return Constants.PROCESS_RESULT_OK;
    }
}
