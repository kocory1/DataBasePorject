package User.dao_user;

import User.model.Camper;
import common.DBConnect;

import java.sql.*;
import java.util.ArrayList;

public class CamperDAO {

    // ✅ 캠핑카 전체 조회
    public ArrayList<Camper> getAllCampers() {
        ArrayList<Camper> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT * FROM camper";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Camper camper = new Camper();
                camper.setCamperId(rs.getInt("camper_id"));
                camper.setName(rs.getString("name"));
                camper.setVehicleNumber(rs.getString("vehicle_number"));
                camper.setSeats(rs.getInt("seats"));
                camper.setImageUrl(rs.getString("image_url"));
                camper.setDetails(rs.getString("details"));
                camper.setRentalFee(rs.getDouble("rental_fee"));
                camper.setRentalCompanyId(rs.getInt("rental_company_id"));
                camper.setRegistrationDate(rs.getDate("registration_date"));
                list.add(camper);
            }

        } catch (Exception e) {
            System.out.println("❌ 캠핑카 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return list;
    }

    // ✅ 대여 가능 여부 확인 (대여 테이블에 해당 camper_id가 있는지 확인)
    public boolean isAvailable(int camperId) {
        boolean result = true;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();

            // 현재 해당 캠핑카의 대여 기록이 있는지 확인
            String sql = "SELECT COUNT(*) as count FROM rental WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                result = (count == 0);  // 대여 기록이 없으면 true (대여 가능)
                System.out.println("✅ 캠핑카 " + camperId + "의 대여 수: " + count + 
                                  (result ? " - 대여 가능합니다." : " - 이미 대여 중입니다."));
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 가능 여부 확인 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return result;
    }
    
    // ✅ 특정 날짜에 대여 가능한지 확인 (대여 테이블에 해당 camper_id가 있는지 확인)
    public boolean isAvailableOnDate(int camperId, Date startDate, int period) {
        boolean result = true;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();

            // 해당 캠핑카의 대여 기록이 있는지 확인
            String sql = "SELECT COUNT(*) as count FROM rental WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                result = (count == 0);  // 대여 기록이 없으면 true (대여 가능)
                System.out.println("✅ 캠핑카 " + camperId + "의 대여 수: " + count + 
                                  (result ? " - 대여 가능합니다." : " - 이미 대여 중입니다."));
            }

        } catch (Exception e) {
            System.out.println("❌ 날짜별 대여 가능 여부 확인 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return result;
    }
}

