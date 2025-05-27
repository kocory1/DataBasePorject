package User.dao_user;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import User.model.MaintenanceRecord;
import common.DBConnect;

public class MaintenanceDAO {

    // ✅ 내부 정비 이력 조회
    public ArrayList<MaintenanceRecord> getInternalMaintenance(int camperId) {
        ArrayList<MaintenanceRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
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
            conn = DBConnect.getUserConnection();
            String sql = "SELECT maintenance_details, repair_date, repair_cost, shop_id, additional_maintenance_details FROM externalmaintenance WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRecord record = new MaintenanceRecord();
                record.setType("외부");
                record.setMaintenanceDate(rs.getDate("repair_date"));
                record.setDetails(rs.getString("maintenance_details"));
                record.setCost(rs.getDouble("repair_cost"));
                record.setAdditionalDetails(rs.getString("additional_maintenance_details"));

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

    // ✅ 모든 정비소 목록 조회
    public ArrayList<String[]> getAllShops() {
        ArrayList<String[]> shopList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
            String sql = "SELECT shop_id, shop_name FROM externalmaintenanceshop ORDER BY shop_name";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] shop = new String[2];
                shop[0] = rs.getString("shop_id");
                shop[1] = rs.getString("shop_name");
                shopList.add(shop);
            }

        } catch (Exception e) {
            System.out.println("정비소 목록 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return shopList;
    }
    
    // ✅ 외부 정비 요청 등록
    public boolean insertExternalMaintenance(int camperId, int shopId, String licenseNumber, 
                                          String details, Date repairDate, double estimatedCost, String additionalDetails) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DBConnect.getUserConnection();
            
            // 렌탈 회사 정보 가져오기
            int rentalCompanyId = getRentalCompanyId(camperId, conn);
            
            // 마지막 external_maintenance_id 가져오기
            int newMaintenanceId = 1;
            String idSql = "SELECT MAX(external_maintenance_id) as max_id FROM externalmaintenance";
            pstmt = conn.prepareStatement(idSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newMaintenanceId = rs.getInt("max_id") + 1;
            }
            rs.close();
            pstmt.close();
            
            // 외부 정비 요청 등록
            String sql = "INSERT INTO externalmaintenance " +
                         "(external_maintenance_id, camper_id, shop_id, rental_company_id, license_number, " +
                         "maintenance_details, repair_date, repair_cost, payment_due_date, additional_maintenance_details) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE_ADD(?, INTERVAL 7 DAY), ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newMaintenanceId);
            pstmt.setInt(2, camperId);
            pstmt.setInt(3, shopId);
            pstmt.setInt(4, rentalCompanyId);
            pstmt.setString(5, licenseNumber);
            pstmt.setString(6, details);
            pstmt.setDate(7, new java.sql.Date(repairDate.getTime()));
            pstmt.setDouble(8, estimatedCost);
            pstmt.setDate(9, new java.sql.Date(repairDate.getTime())); // 지불 기한은 수리일로부터 7일 후
            pstmt.setString(10, additionalDetails);
            
            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);
            
            System.out.println(success ? "✅ 외부 정비 요청 등록 성공" : "❌ 외부 정비 요청 등록 실패");
            
        } catch (Exception e) {
            System.out.println("❌ 외부 정비 요청 등록 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return success;
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
    
    // ✅ 캠핑카의 렌탈 회사 ID 조회
    private int getRentalCompanyId(int camperId, Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int rentalCompanyId = 1; // 기본값

        try {
            String sql = "SELECT rental_company_id FROM camper WHERE camper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, camperId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                rentalCompanyId = rs.getInt("rental_company_id");
            }

        } catch (Exception e) {
            System.out.println("렌탈 회사 ID 조회 오류: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        }

        return rentalCompanyId;
    }
}