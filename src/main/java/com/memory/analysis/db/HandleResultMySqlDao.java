package com.memory.analysis.db;

import com.memory.analysis.entity.HandleResultEntity;
import com.memory.analysis.utils.Constants;

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
public class HandleResultMySqlDao extends HandleResultDao<HandleResultEntity> {

    @Override
    public List<HandleResultEntity> query(String sqlStr) {
        List<HandleResultEntity> handleResultEntityList = new ArrayList<>();
        Statement statement;
        try {
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlStr);
            while (resultSet.next()) {
                HandleResultEntity handleResultEntity = new HandleResultEntity();
                handleResultEntity.fileName = resultSet.getString(Constants.HANDLE_RESULT_TABLE_FILE_NAME);
                handleResultEntity.handleType = resultSet.getInt(Constants.HANDLE_RESULT_TABLE_HANDLE_TYPE);
                handleResultEntity.status = resultSet.getInt(Constants.HANDLE_RESULT_TABLE_STATUS);
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
    public void add(HandleResultEntity handleResultEntity) {
        PreparedStatement ptmt = null;
        StringBuilder addSql = new StringBuilder("INSERT INTO ").
                append(getTableName()).
                append(" (").
                append(Constants.HANDLE_RESULT_TABLE_FILE_NAME + ", ").
                append(Constants.HANDLE_RESULT_TABLE_HANDLE_TYPE + ", ").
                append(Constants.HANDLE_RESULT_TABLE_STATUS + ")").
                append(" VALUES(?,?,?)");

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
    public void update() {

    }
}
