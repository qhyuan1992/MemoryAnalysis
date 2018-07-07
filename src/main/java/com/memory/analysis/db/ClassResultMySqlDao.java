package com.memory.analysis.db;

import com.memory.analysis.entity.ClassResultEntity;
import com.memory.analysis.process.ClassObjWrapper;
import com.memory.analysis.utils.FormatUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author cainjiang
 * @date 2018/6/3
 */
public class ClassResultMySqlDao extends ClassResultDao {

    @Override
    public void handle(ClassObjWrapper classObjWrapper, String hprofFileName) {
        synchronized (this) {
            ClassResultEntity classResultEntity = query(classObjWrapper.classObj.getClassName());
            if (classResultEntity != null) {
                update(classResultEntity, hprofFileName, classObjWrapper.instanceCount, classObjWrapper.retainedHeapSize);
            } else {
                add(classObjWrapper.classObj.getClassName(), classObjWrapper.instanceCount, classObjWrapper.retainedHeapSize, hprofFileName);
            }
        }
    }

    @Override
    public ClassResultEntity query(String className) {
        ClassResultEntity classResultEntity = null;
        StringBuilder queryStrSql = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE object_name = '").append(className).append("'");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryStrSql.toString());
            if (rs.next()) {
                classResultEntity = new ClassResultEntity();
                classResultEntity.objectName = className;
                classResultEntity.sumNum = rs.getDouble("sum_num");
                classResultEntity.aveNum = rs.getDouble("ave_num");
                classResultEntity.maxNum = rs.getDouble("max_num");
                classResultEntity.maxNumFileName = rs.getString("max_num_file_name");
                classResultEntity.sumRetained = rs.getDouble("sum_retained");
                classResultEntity.aveRetained = rs.getDouble("ave_retained");
                classResultEntity.maxRetained = rs.getDouble("max_retained");
                classResultEntity.maxRetainedFileName = rs.getString("max_retained_file_name");
                System.out.println(className + " exist: " + classResultEntity.toString());
            } else {
                System.out.println(className + " don't exist!");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classResultEntity;
    }

    @Override
    public void add(String objectStr, double currentNum, double currentRetained, String hprofFileName) {
//预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder addSql = new StringBuilder("INSERT INTO ").append(getTableName()).append(" (object_name, sum_num, ave_num, max_num, " +
                "max_num_file_name, sum_retained, ave_retained, max_retained, max_retained_file_name ) ").append("VALUES" + " (?,?,?,?,?,?,?,?,?)");
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
    public void update(ClassResultEntity classResultEntity, String hprofFileName, double currentNum, double currentRetained) {
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = null;
        StringBuilder updateSql = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ").append("sum_num = ?, max_num = ?, " +
                "max_num_file_name = ?, sum_retained = ?, max_retained = ?, max_retained_file_name = ? ").append("WHERE object_name = ?");
        try {
            ptmt = conn.prepareStatement(updateSql.toString());
            ptmt.setDouble(1, classResultEntity.sumNum + currentNum);
            ptmt.setDouble(2, classResultEntity.maxNum < currentNum ? currentNum : classResultEntity.maxNum);
            ptmt.setString(3, classResultEntity.maxNum < currentNum ? hprofFileName : classResultEntity.maxNumFileName);
            ptmt.setDouble(4, classResultEntity.sumRetained + currentRetained);
            ptmt.setDouble(5, classResultEntity.maxRetained < currentRetained ? currentRetained : classResultEntity.maxRetained);
            ptmt.setString(6, classResultEntity.maxRetained < currentRetained ? hprofFileName : classResultEntity.maxRetainedFileName);
            ptmt.setString(7, classResultEntity.objectName);
            //执行
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
