package com.memory.analysis.db;

import com.memory.analysis.entity.InstanceResultEntity;
import com.memory.analysis.process.InstanceWrapper;

import java.sql.Connection;
import java.util.HashMap;

/**
 * @author cainjiang
 * @date 2018/6/1
 */
public abstract class InstanceResultDao {
    public HashMap<Long, Connection> connectionHashMap = new HashMap<>();
    public Connection conn;
    public String tableName = "instance_result_table";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setConn(Connection conn, long threadId) {
        this.conn = conn;
        connectionHashMap.put(threadId, conn);
    }

    public Connection getConn(long threadId) {
        Connection connection = connectionHashMap.get(threadId);
        connectionHashMap.remove(threadId);
        return connection;
    }

    public abstract void handle(InstanceWrapper instanceWrapper, String hprofFileName);

    public abstract InstanceResultEntity query(String objectStr);

    public abstract void add(String objectStr, String objectAddressID, int num, double currentLeak, String maxLeakFileName, String gcRoot);

    public abstract void update(InstanceResultEntity memoryLeakEntity, String hprofFileName, double currentLeak, String gcRoot);
}
