// common/DBConnect.java
package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결 관리 유틸리티
 */
public class DBConnect {


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC 드라이버를 찾을 수 없습니다.", e);
        }
    }

    public static Connection getRootConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/camping_car_db", "root","kingpin100"); // JDBC 연결
    }

    public static Connection getUserConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/camping_car_db", "user1","user1"); // JDBC 연결
    }

    public static boolean testConnection() {
        try (Connection conn = getRootConnection()) {
            System.out.println("✅ 데이터베이스 연결 테스트 성공");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ 데이터베이스 연결 테스트 실패: " + e.getMessage());
            return false;
        }
    }
}

