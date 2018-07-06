package com.memory.analysis.db;

import com.memory.analysis.entity.HandleResultEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cainjiang
 * @date 2018/7/6
 */
public class HandleResultMySqlDao extends HandleResultDao{

    @Override
    protected List<HandleResultEntity> query(String sqlStr) {
        List<HandleResultEntity> handleResultEntityList = new ArrayList<>();
        Statement statement;
        try {
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlStr);
            while (resultSet.next()) {
                HandleResultEntity handleResultEntity = new HandleResultEntity();
                handleResultEntity.fileName = resultSet.getString("file_name");
                handleResultEntity.handleType = resultSet.getInt("handle_type");
                handleResultEntity.status = resultSet.getInt("status");
                handleResultEntityList.add(handleResultEntity);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return handleResultEntityList;
    }

    @Override
    protected void add(HandleResultEntity handleResultEntity) {
        PreparedStatement ptmt = null;
        StringBuilder addSql = new StringBuilder("INSERT INTO ").append(getTableName()).append(" (file_name, handle_type, status) ").append("VALUES(?,?,?)");
        try {
            ptmt = conn.prepareStatement(addSql.toString());
            ptmt.setString(1, handleResultEntity.fileName);
            ptmt.setInt(2, handleResultEntity.handleType);
            ptmt.setInt(3, handleResultEntity.status);
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void update() {

    }
}
