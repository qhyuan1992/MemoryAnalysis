package com.memory.analysis.db;

import com.memory.analysis.utils.CheckerUtil;
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
public class InstanceResultMySqlDao extends InstanceResultDao<InstanceResultEntity> {

    @Override
    public void handle(InstanceWrapper instanceWrapper, String hprofFileName) {
        synchronized (this) {
            StringBuilder queryStrSql = new StringBuilder("SELECT * FROM ").
                    append(getTableName()).
                    append(" WHERE " + Constants.INSTANCE_RESULT_TABLE_OBJECT_NAME+ " = '").
                    append(instanceWrapper.classObj.getClassName()).
                    append("'");
            List<InstanceResultEntity> instanceResultEntityList = query(queryStrSql.toString());
            if (!CheckerUtil.isEmpty(instanceResultEntityList)) {
                update(instanceResultEntityList.get(0), hprofFileName, instanceWrapper.retainedHeapSize, instanceWrapper.leakTrace.toString());
            } else {
                add(instanceWrapper.classObj.getClassName(), FormatUtil.formatAddr(instanceWrapper.id), 1, instanceWrapper.retainedHeapSize, hprofFileName, instanceWrapper.leakTrace.toString());
            }
        }
    }

    @Override
    public List<InstanceResultEntity> query(String sqlStr) {
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
        StringBuilder addSql = new StringBuilder("INSERT INTO ").append(getTableName()).
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
    public void update(InstanceResultEntity object, String hprofFileName, double currentLeak, String gcRoot) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder updateSql = new StringBuilder("UPDATE ").
                append(getTableName()).
                append(" SET ").
                append(Constants.INSTANCE_RESULT_TABLE_SUM_NUM + " = ?, ").
                append(Constants.INSTANCE_RESULT_TABLE_SUM_LEAK + " = ?, ").
                append(Constants.INSTANCE_RESULT_TABLE_AVE_LEAK + " = ?, ").
                append(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK + " = ?, ").
                append(Constants.INSTANCE_RESULT_TABLE_MAX_LEAK_FILE_NAME + " = ?, ").
                append(Constants.INSTANCE_RESULT_TABLE_GC_ROOT + " = ?").
                append(" WHERE ").
                append(Constants.INSTANCE_RESULT_TABLE_OBJECT_NAME + " = ?");

        try {
            ptmt = conn.prepareStatement(updateSql.toString());
            ptmt.setInt(1, object.num + 1);
            ptmt.setDouble(2, object.sumLeak + currentLeak);
            ptmt.setDouble(3, (object.sumLeak + currentLeak) / (object.num + 1));
            ptmt.setDouble(4, object.maxLeak < currentLeak ? currentLeak : object.maxLeak);
            ptmt.setString(5, object.maxLeak < currentLeak ? hprofFileName : object.maxLeakFileName);
            ptmt.setString(6, object.maxLeak < currentLeak ? gcRoot : object.gcRoot);
            ptmt.setString(7, object.objectName);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
