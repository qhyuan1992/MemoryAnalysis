package com.memory.analysis.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author cainjiang
 * @date 2018/6/3
 */
public class ConnectionUtil {
    //URL指向要访问的数据库名
    public static final String URL = "jdbc:mysql://localhost:3306/auto_memory_analyze_result_db?useUnicode=true&characterEncoding=utf-8";
    //MySQL配置时的用户名
    public static final String USER = "root";
    //MySQL配置时的密码
    public static final String PASSWORD = "jiang12315";
    //驱动程序名
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    public Connection getConnection() {
        Connection connection = null;
        try {
            //1.加载驱动程序
            Class.forName(DRIVER);
            //2. 获得数据库连接
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
