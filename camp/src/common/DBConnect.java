// common/DBConnect.java
package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결 관리 유틸리티
 */
public class DBConnect {

    public static Connection getRootConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/camping_car_db", "root","kingpin100"
                  + ""); // JDBC 연결
            System.out.println("DB 연결 완료");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            throw new SQLException("드라이버 로드 오류", e);
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            throw e;
        }
    }

    public static Connection getUserConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/camping_car_db", "user1","user1"
                  + ""); // JDBC 연결
            System.out.println("DB 연결 완료");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            throw new SQLException("드라이버 로드 오류", e);
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            throw e;
        }
    }

    public static boolean testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/camping_car_db", "root","kingpin100"
                  + ""); // JDBC 연결
            System.out.println("DB 연결 완료");
            conn.close();
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            return false;
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            return false;
        }
    }
}

