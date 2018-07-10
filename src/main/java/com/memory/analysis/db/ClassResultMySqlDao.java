package com.memory.analysis.db;

import com.memory.analysis.entity.ClassResultEntity;
import com.memory.analysis.process.ClassObjWrapper;
import com.memory.analysis.utils.CheckerUtil;
import com.memory.analysis.utils.Constants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cainjiang
 * @date 2018/6/3
 */
public class ClassResultMySqlDao extends ClassResultDao<ClassResultEntity> {

    @Override
    public void handle(ClassObjWrapper classObjWrapper, String hprofFileName) {
        synchronized (this) {
            StringBuilder queryStrSql = new StringBuilder("SELECT * FROM ").
                    append(getTableName()).
                    append(" WHERE ").
                    append(Constants.CLASS_RESULT_TABLE_OBJECT_NAME + " = '").
                    append(classObjWrapper.classObj.getClassName()).
                    append("'");
            List<ClassResultEntity> classResultEntityList = query(queryStrSql.toString());
            if (!CheckerUtil.isEmpty(classResultEntityList)) {
                update(classResultEntityList.get(0), hprofFileName, classObjWrapper.instanceCount, classObjWrapper.retainedHeapSize);
            } else {
                add(classObjWrapper.classObj.getClassName(), classObjWrapper.instanceCount, classObjWrapper.retainedHeapSize, hprofFileName);
            }
        }
    }

    @Override
    public List<ClassResultEntity> query(String sqlStr) {
        List<ClassResultEntity> resultList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlStr);
            while (resultSet.next()) {
                ClassResultEntity classResultEntity = new ClassResultEntity();
                classResultEntity.objectName = resultSet.getString(Constants.CLASS_RESULT_TABLE_OBJECT_NAME);
                classResultEntity.sumNum = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_SUM_NUM);
                classResultEntity.aveNum = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_AVE_NUM);
                classResultEntity.maxNum = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_MAX_NUM);
                classResultEntity.maxNumFileName = resultSet.getString(Constants.CLASS_RESULT_TABLE_MAX_NUM_FILE_NAME);
                classResultEntity.sumRetained = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_SUM_RETAINED);
                classResultEntity.aveRetained = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_AVE_RETAINED);
                classResultEntity.maxRetained = resultSet.getDouble(Constants.CLASS_RESULT_TABLE_MAX_RETAINED);
                classResultEntity.maxRetainedFileName = resultSet.getString(Constants.CLASS_RESULT_TABLE_MAX_RETAINED_FILE_NAME);
                resultList.add(classResultEntity);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public void add(String objectStr, double currentNum, double currentRetained, String hprofFileName) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder addSql = new StringBuilder("INSERT INTO ").
                append(getTableName()).
                append(" (").
                append(Constants.CLASS_RESULT_TABLE_OBJECT_NAME + ", ").
                append(Constants.CLASS_RESULT_TABLE_SUM_NUM + ", ").
                append(Constants.CLASS_RESULT_TABLE_AVE_NUM + ", ").
                append(Constants.CLASS_RESULT_TABLE_MAX_NUM + ", ").
                append(Constants.CLASS_RESULT_TABLE_MAX_NUM_FILE_NAME + ", ").
                append(Constants.CLASS_RESULT_TABLE_SUM_RETAINED + ", ").
                append(Constants.CLASS_RESULT_TABLE_AVE_RETAINED + ", ").
                append(Constants.CLASS_RESULT_TABLE_MAX_RETAINED + ", ").
                append(Constants.CLASS_RESULT_TABLE_MAX_RETAINED_FILE_NAME + ")").
                append(" VALUES").append(" (?,?,?,?,?,?,?,?,?)");

        try {
            ptmt = conn.prepareStatement(addSql.toString());
            ptmt.setString(1, objectStr);
            ptmt.setDouble(2, currentNum);
            ptmt.setDouble(3, 0.0);
            ptmt.setDouble(4, currentNum);
            ptmt.setString(5, hprofFileName);
            ptmt.setDouble(6, currentRetained);
            ptmt.setDouble(7, 0.0);
            ptmt.setDouble(8, currentRetained);
            ptmt.setString(9, hprofFileName);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ClassResultEntity object, String hprofFileName, double currentNum, double currentRetained) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder updateSql = new StringBuilder("UPDATE ").
                append(getTableName()).
                append(" SET ").
                append(Constants.CLASS_RESULT_TABLE_SUM_NUM + " = ?, ").
                append(Constants.CLASS_RESULT_TABLE_MAX_NUM + " = ?, ").
                append(Constants.CLASS_RESULT_TABLE_MAX_NUM_FILE_NAME + " = ?, ").
                append(Constants.CLASS_RESULT_TABLE_SUM_RETAINED + " = ?, ").
                append(Constants.CLASS_RESULT_TABLE_MAX_RETAINED + " = ?, ").
                append(Constants.CLASS_RESULT_TABLE_MAX_RETAINED_FILE_NAME + " = ?").
                append(" WHERE ").
                append(Constants.CLASS_RESULT_TABLE_OBJECT_NAME + " = ?");

        try {
            ptmt = conn.prepareStatement(updateSql.toString());
            ptmt.setDouble(1, object.sumNum + currentNum);
            ptmt.setDouble(2, object.maxNum < currentNum ? currentNum : object.maxNum);
            ptmt.setString(3, object.maxNum < currentNum ? hprofFileName : object.maxNumFileName);
            ptmt.setDouble(4, object.sumRetained + currentRetained);
            ptmt.setDouble(5, object.maxRetained < currentRetained ? currentRetained : object.maxRetained);
            ptmt.setString(6, object.maxRetained < currentRetained ? hprofFileName : object.maxRetainedFileName);
            ptmt.setString(7, object.objectName);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
