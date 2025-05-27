package User.dao_user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.DBConnect;

public class CustomerDAO {

    // ✅ 사용자 로그인: username + password 일치 여부 확인
    public boolean login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT * FROM customer WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                isValid = true;
            }

        } catch (Exception e) {
            System.out.println("❌ 로그인 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return isValid;
    }
}

