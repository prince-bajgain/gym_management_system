package com.gym.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gym_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "prince";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}