package com.pahanaedu.test;

import com.pahanaedu.util.DbConnector;

import java.sql.Connection;

public class TestDb {
    public static void main(String[] args) {
        Connection conn = DbConnector.getConnection();
        if (conn != null) {
            System.out.println("Connected to MySQL");
        } else {
            System.out.println("Connection failed.");
        }
    }
}
