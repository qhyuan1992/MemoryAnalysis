package com.memory.analysis.db;

import com.memory.analysis.entity.ClassResultEntity;
import com.memory.analysis.entity.HandleResultEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author cainjiang
 * @date 2018/5/29
 */
public abstract class HandleResultDao<T>{
    public HashMap<Long, Connection> connectionHashMap = new HashMap<>();
    public Connection conn;
    public String tableName = "handle_result_table";

    public void setConn(Connection conn, long threadId) {
        this.conn = conn;
        connectionHashMap.put(threadId, conn);
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

    public abstract List<T> query(String sqlStr);

    public abstract void add(T object);

    public abstract void update();
}
