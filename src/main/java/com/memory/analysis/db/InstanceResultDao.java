package com.memory.analysis.db;

import com.memory.analysis.process.InstanceWrapper;
import com.memory.analysis.utils.Constants;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * @author cainjiang
 * @date 2018/6/1
 */
public abstract class InstanceResultDao<T> {
    public HashMap<Long, Connection> connectionHashMap = new HashMap<>();
    public Connection conn;
    public String tableName = Constants.INSTANCE_RESULT_TABLE;

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

    public abstract List<T> query(String sqlStr);

    public abstract void add(String objectStr, String objectAddressID, int num, double currentLeak, String maxLeakFileName, String gcRoot);

    public abstract void update(T object, String hprofFileName, double currentLeak, String gcRoot);
}
