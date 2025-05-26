package dao_user;

import dbConnect.Db1;
import model.Camper;

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
            conn = Db1.getUserConnection();
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

    // ✅ 대여 가능 여부 확인 (현재 시점 기준으로 rental 기간이 겹치면 대여 불가)
    public boolean isAvailable(int camperId) {
        boolean result = true;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Db1.getUserConnection();

            String sql = "SELECT * FROM rental " +
                         "WHERE camper_id = ? " +
                         "AND NOW() < DATE_ADD(rental_start_date, INTERVAL rental_period DAY)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = false;  // 현재 대여 중이면 false
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
}

