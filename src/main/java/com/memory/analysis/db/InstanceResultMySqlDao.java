package com.memory.analysis.db;

import com.memory.analysis.utils.Constants;
import com.memory.analysis.entity.InstanceResultEntity;
import com.memory.analysis.process.InstanceWrapper;
import com.memory.analysis.utils.FormatUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cainjiang
 * @date 2018/6/1
 */
public class InstanceResultMySqlDao extends InstanceResultDao {

    @Override
    public void handle(InstanceWrapper instanceWrapper, String hprofFileName) {
        synchronized (this) {
            InstanceResultEntity instanceResultEntity = query(instanceWrapper.classObj.getClassName());
            if (instanceResultEntity != null) {
                update(instanceResultEntity, hprofFileName, instanceWrapper.retainedHeapSize, instanceWrapper.leakTrace.toString());
            } else {
                add(instanceWrapper.classObj.getClassName(), FormatUtil.formatAddr(instanceWrapper.id), 1, instanceWrapper.retainedHeapSize, hprofFileName, instanceWrapper.leakTrace.toString());
            }
        }
    }

    @Override
    public InstanceResultEntity query(String objectStr) {
        InstanceResultEntity instanceResultEntity = null;
        StringBuilder queryStrSql = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE object_name = '").append(objectStr).append("'");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryStrSql.toString());
            if (rs.next()) {
                instanceResultEntity = new InstanceResultEntity();
                instanceResultEntity.objectName = objectStr;
                instanceResultEntity.num = rs.getInt("sum_num");
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

    @Override
    public List<InstanceResultEntity> queryResult(String sqlStr) {
        List<InstanceResultEntity> resultList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlStr);
            while (resultSet.next()) {
                InstanceResultEntity instanceResultEntity = new InstanceResultEntity();
                instanceResultEntity.objectName = resultSet.getString(Constants.INSTANCE_RESULT_TABLE_OBJECT_NAME);
                instanceResultEntity.objectAddressID = resultSet.getString(Constants.INSTANCE_RESULT_TABLE_OBJECT_ADDRESS_ID);
                instanceResultEntity.num = resultSet.getInt(Constants.INSTANCE_RESULT_TABLE_SUM_NUM);
                instanceResultEntity.sumLeak = resultSet.getDouble(Constants.INSTANCE_RESULT_TABLE_SUM_LEAK);
                instanceResultEntity.aveLeak = resultSet.getDouble(Constants.INSTANCE_RESULT_TABLE_AVE_LEAK);
                instanceResultEntity.maxLeak = resultSet.getDouble(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK);
                instanceResultEntity.maxLeakFileName = resultSet.getString(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK_FILE_NAME);
                instanceResultEntity.gcRoot = resultSet.getString(Constants.INSTANCE_RESULT_TABLE_GC_ROOT);
                resultList.add(instanceResultEntity);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public void add(String objectStr, String objectAddressID, int num, double currentLeak, String maxLeakFileName, String gcRoot) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
//        StringBuilder addSql = new StringBuilder("INSERT INTO ").append(getTableName()).append(" (object_name, object_address_id, sum_num, " +
//                "sum_leak, " + "ave_leak, max_leak, " + "max_leak_file_name, gc_root) ").append("VALUES (?,?,?,?,?,?,?,?)");
        StringBuilder addSql = new StringBuilder("INSERT INTO").append(getTableName()).
                append(" (").
                append(Constants.INSTANCE_RESULT_TABLE_OBJECT_NAME + ", ").
                append(Constants.INSTANCE_RESULT_TABLE_OBJECT_ADDRESS_ID + ", ").
                append(Constants.INSTANCE_RESULT_TABLE_SUM_NUM+", ").
                append(Constants.INSTANCE_RESULT_TABLE_SUM_LEAK + ", ").
                append(Constants.INSTANCE_RESULT_TABLE_AVE_LEAK + ", ").
                append(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK +", ").
                append(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK_FILE_NAME + ", ").
                append(Constants.INSTANCE_RESULT_TABLE_GC_ROOT).
                append(") ").
                append("VALUES (?,?,?,?,?,?,?,?)");

        try {
            ptmt = conn.prepareStatement(addSql.toString());
            ptmt.setString(1, objectStr);
            ptmt.setString(2, objectAddressID);
            ptmt.setInt(3, num);
            ptmt.setDouble(4, currentLeak);
            ptmt.setDouble(5, currentLeak);
            ptmt.setDouble(6, currentLeak);
            ptmt.setString(7, maxLeakFileName);
            ptmt.setString(8, gcRoot);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(InstanceResultEntity memoryLeakEntity, String hprofFileName, double currentLeak, String gcRoot) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder updateSql = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ").append("sum_num = ?, sum_leak = ?, ave_leak =" +
                " ?, " + "max_leak = ?, max_leak_file_name = ?, gc_root = ? ").append("WHERE object_name = ?");
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
}
