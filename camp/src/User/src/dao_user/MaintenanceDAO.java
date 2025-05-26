package dao_user;

import java.sql.*;
import java.util.ArrayList;

import dbConnect.Db1;
import model.MaintenanceRecord;

public class MaintenanceDAO {

    // ✅ 내부 정비 이력 조회
    public ArrayList<MaintenanceRecord> getInternalMaintenance(int camperId) {
        ArrayList<MaintenanceRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Db1.getUserConnection();
            String sql = "SELECT maintenance_date, maintenance_duration_minutes FROM internalmaintenance WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecord record = new MaintenanceRecord();
                record.setType("내부");
                record.setMaintenanceDate(rs.getDate("maintenance_date"));
                record.setDurationMinutes(rs.getInt("maintenance_duration_minutes"));
                record.setDetails("내부 정비 기록"); // 상세 내용 없으므로 고정
                list.add(record);
            }

        } catch (Exception e) {
            System.out.println("내부 정비 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return list;
    }

    // ✅ 외부 정비 이력 조회
    public ArrayList<MaintenanceRecord> getExternalMaintenance(int camperId) {
        ArrayList<MaintenanceRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Db1.getUserConnection();
            String sql = "SELECT maintenance_details, repair_date, repair_cost, shop_id FROM externalmaintenance WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecord record = new MaintenanceRecord();
                record.setType("외부");
                record.setMaintenanceDate(rs.getDate("repair_date"));
                record.setDetails(rs.getString("maintenance_details"));
                record.setCost(rs.getDouble("repair_cost"));

                // shop_id → shop_name 조회 (optional join)
                int shopId = rs.getInt("shop_id");
                String shopName = getShopNameById(shopId, conn);
                record.setShopName(shopName);

                list.add(record);
            }

        } catch (Exception e) {
            System.out.println("외부 정비 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return list;
    }

    // ✅ 정비소 이름 조회 (shop_id → externalmaintenanceshop)
    private String getShopNameById(int shopId, Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";

        try {
            String sql = "SELECT shop_name FROM externalmaintenanceshop WHERE shop_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, shopId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                name = rs.getString("shop_name");
            }

        } catch (Exception e) {
            System.out.println("정비소 이름 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        }

        return name;
    }
}
