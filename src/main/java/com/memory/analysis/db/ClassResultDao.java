package com.memory.analysis.db;

import com.memory.analysis.entity.ClassResultEntity;
import com.memory.analysis.process.ClassObjWrapper;

import java.sql.Connection;
import java.util.HashMap;

/**
 * @author cainjiang
 * @date 2018/6/3
 */
public abstract class ClassResultDao {
    public HashMap<Long,Connection> connectionHashMap= new HashMap<>();
    public Connection conn;
    public String tableName = "class_result_table";

    public void setConn(Connection conn,long threadId) {
        this.conn = conn;
        connectionHashMap.put(threadId,conn);
    }

    public Connection getConn(long threadId) {
        Connection connection = connectionHashMap.get(threadId);
        connectionHashMap.remove(threadId);
        return connection;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public abstract void handle(ClassObjWrapper classObjWrapper, String hprofFileName);

    public abstract ClassResultEntity query(String className);

    public abstract void add(String objectStr, double currentNum, double currentRetained, String hprofFileName);

    public abstract void update(ClassResultEntity classResultEntity, String hprofFileName, double currentNum, double currentRetained);
}
