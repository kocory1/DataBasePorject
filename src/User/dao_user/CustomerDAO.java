package User.dao_user;

import java.sql.*;

import common.DBConnect;
import User.model.Rental;

import java.util.ArrayList;

/**
 * CustomerDAO
 *
 * 1) boolean login(String username, String password)
 *    → LoginView 또는 CustomerMainView 진입 시 사용
 * 2) ArrayList<Rental> getMyRentals(String licenseNumber)
 *    → CustomerMainView에서 “내 대여 내역” 로드 시 래퍼로 사용
 */
public class CustomerDAO {

    /**
     * 1) 로그인 검증
     */
    public boolean login(String username, String password) {
        String sql = "SELECT COUNT(*) AS cnt FROM Customer WHERE username = ? AND password = ?";
        boolean isValid = false;

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isValid = (rs.getInt("cnt") == 1);
                }
            }
        } catch (SQLException e) {
            System.out.println("로그인 오류: " + e.getMessage());
        }
        return isValid;
    }

    /**
     * 2) 내 대여 내역 조회 래퍼 메서드
     *    → RentalDAO.getRentalsByLicense(licenseNumber) 호출
     */
    public ArrayList<Rental> getMyRentals(String licenseNumber) throws SQLException {
        RentalDAO rDao = new RentalDAO();
        return rDao.getRentalsByLicense(licenseNumber);
    }
}
