package com.memory.analysis.db;

import com.memory.analysis.entity.InstanceResultEntity;
import com.memory.analysis.utils.DBUtil;

import java.sql.*;

/**
 * @author cainjiang
 * @date 2018/5/28
 */
public class InstanceDBDao {

    Connection conn;
    StringBuilder addSql = new StringBuilder("INSERT INTO ").append("result_table ").append("(object_name, num, sum_leak, ave_leak, max_leak, " +
            "max_leak_file_name, gc_root) ").append("VALUES (?,?,?,?,?,?,?)");
    StringBuilder updateSql = new StringBuilder("UPDATE ").append("result_table ").append("SET ").append("num = ?, sum_leak = ?, ave_leak = ?, " +
            "max_leak = ?, max_leak_file_name = ?, gc_root = ? ").append("WHERE object_name = ?");

    public InstanceDBDao() {
        conn = DBUtil.getConnection();
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public InstanceResultEntity query(String objectStr) {
        InstanceResultEntity instanceResultEntity = null;
        StringBuilder queryStrSql = new StringBuilder("SELECT * FROM result_table WHERE object_name = '").append(objectStr).append("'");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryStrSql.toString());
            if (rs.next()) {
                instanceResultEntity = new InstanceResultEntity();
                instanceResultEntity.objectName = objectStr;
                instanceResultEntity.num = rs.getInt("num");
                instanceResultEntity.sumLeak = rs.getDouble("sum_leak");
                instanceResultEntity.aveLeak = rs.getDouble("ave_leak");
                instanceResultEntity.maxLeak = rs.getDouble("max_leak");
                instanceResultEntity.maxLeakFileName = rs.getString("max_leak_file_name");
                instanceResultEntity.gcRoot = rs.getString("gc_root");
                System.out.println(objectStr + " exist: " + instanceResultEntity.toString());
            } else {
                System.out.println(objectStr + " don't exist!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instanceResultEntity;
    }

    public void update(InstanceResultEntity memoryLeakEntity, String hprofFileName, double currentLeak, String gcRoot) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        try {
            ptmt = conn.prepareStatement(updateSql.toString());
            ptmt.setInt(1, memoryLeakEntity.num + 1);
            ptmt.setDouble(2, memoryLeakEntity.sumLeak + currentLeak);
            ptmt.setDouble(3, (memoryLeakEntity.sumLeak + currentLeak) / (memoryLeakEntity.num + 1));
            ptmt.setDouble(4, memoryLeakEntity.maxLeak < currentLeak ? currentLeak : memoryLeakEntity.maxLeak);
            ptmt.setString(5, memoryLeakEntity.maxLeak < currentLeak ? hprofFileName : memoryLeakEntity.maxLeakFileName);
            ptmt.setString(6, memoryLeakEntity.maxLeak < currentLeak ? gcRoot : memoryLeakEntity.gcRoot);
            ptmt.setString(7, memoryLeakEntity.objectName);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void add(String objectStr, int num, double currentLeak, String maxLeakFileName, String gcRoot) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        try {
            ptmt = conn.prepareStatement(addSql.toString());
            ptmt.setString(1, objectStr);
            ptmt.setInt(2, num);
            ptmt.setDouble(3, currentLeak);
            ptmt.setDouble(4, currentLeak);
            ptmt.setDouble(5, currentLeak);
            ptmt.setString(6, maxLeakFileName);
            ptmt.setString(7, gcRoot);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
