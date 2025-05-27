package User.dao_user;

import User.model.Rental;
import common.DBConnect;

import java.sql.*;
import java.util.ArrayList;

public class RentalDAO {

    // ✅ 1. 대여 등록
    public void insertRental(Rental rental) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
            
            // 먼저 새 rental_id 가져오기
            String idSql = "SELECT MAX(rental_id) as max_id FROM rental";
            pstmt = conn.prepareStatement(idSql);
            rs = pstmt.executeQuery();
            
            int newRentalId = 1;
            if (rs.next() && rs.getObject("max_id") != null) {
                newRentalId = rs.getInt("max_id") + 1;
            }
            
            System.out.println("✅ 새 대여 등록: 신규 ID=" + newRentalId + 
                              ", 캠핑카ID=" + rental.getCamperId() + 
                              ", 라이센스=" + rental.getLicenseNumber());
            
            rs.close();
            pstmt.close();
            
            // 대여 정보 삽입
            String sql = "INSERT INTO rental (rental_id, camper_id, license_number, rental_company_id, " +
                         "rental_start_date, rental_period, bill_amount, payment_due_date, " +
                         "additional_charges_description, additional_charges_amount) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newRentalId);
            pstmt.setInt(2, rental.getCamperId());
            pstmt.setString(3, rental.getLicenseNumber());
            pstmt.setInt(4, rental.getRentalCompanyId());
            pstmt.setDate(5, rental.getRentalStartDate());
            pstmt.setInt(6, rental.getRentalPeriod());
            pstmt.setDouble(7, rental.getBillAmount());
            pstmt.setDate(8, rental.getPaymentDueDate());
            pstmt.setString(9, rental.getAdditionalChargesDescription());
            pstmt.setDouble(10, rental.getAdditionalChargesAmount());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ 대여 등록 완료: " + (rowsAffected > 0 ? "성공" : "실패"));
            
            // 등록 후 다시 확인
            pstmt.close();
            String checkSql = "SELECT * FROM rental WHERE rental_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, newRentalId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("   👉 등록된 대여 확인: ID=" + rs.getInt("rental_id") + 
                                  ", 캠핑카ID=" + rs.getInt("camper_id") + 
                                  ", 라이센스=" + rs.getString("license_number"));
            } else {
                System.out.println("   ⚠️ 대여 등록 후 확인 실패: ID=" + newRentalId);
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 등록 오류: " + e.getMessage());
            e.printStackTrace(); // 디버깅용 스택 트레이스 출력
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // ✅ 2. 내 대여 내역 조회 (license_number로)
    public ArrayList<Rental> getRentalsByLicense(String licenseNumber) {
        ArrayList<Rental> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
            
            System.out.println("✅ 대여 내역 조회 요청: licenseNumber=" + licenseNumber);
            
            String sql = "SELECT * FROM rental WHERE license_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, licenseNumber);
            rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                Rental r = new Rental();
                r.setRentalId(rs.getInt("rental_id"));
                r.setCamperId(rs.getInt("camper_id"));
                r.setLicenseNumber(rs.getString("license_number"));
                r.setRentalCompanyId(rs.getInt("rental_company_id"));
                r.setRentalStartDate(rs.getDate("rental_start_date"));
                r.setRentalPeriod(rs.getInt("rental_period"));
                r.setBillAmount(rs.getDouble("bill_amount"));
                r.setPaymentDueDate(rs.getDate("payment_due_date"));
                r.setAdditionalChargesDescription(rs.getString("additional_charges_description"));
                r.setAdditionalChargesAmount(rs.getDouble("additional_charges_amount"));
                
                System.out.println("   👉 대여 레코드 " + count + ": ID=" + r.getRentalId() + 
                                  ", 캠핑카ID=" + r.getCamperId() + 
                                  ", 라이센스=" + r.getLicenseNumber());
                
                list.add(r);
            }
            
            System.out.println("✅ 대여 내역 조회 결과: " + count + "개 레코드 찾음");

        } catch (Exception e) {
            System.out.println("❌ 대여 조회 오류: " + e.getMessage());
            e.printStackTrace(); // 디버깅용 스택 트레이스 출력
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return list;
    }

    // ✅ 3. 대여 수정
    public void updateRental(Rental rental) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnect.getUserConnection();
            String sql = "UPDATE rental SET rental_period = ?, bill_amount = ?, payment_due_date = ? WHERE rental_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rental.getRentalPeriod());
            pstmt.setDouble(2, rental.getBillAmount());
            pstmt.setDate(3, rental.getPaymentDueDate());
            pstmt.setInt(4, rental.getRentalId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 대여 정보 수정 완료");
            } else {
                System.out.println("❌ 해당 rental_id 없음 (수정 실패)");
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 수정 오류: " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // ✅ 4. 대여 삭제
    public void deleteRental(int rentalId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getUserConnection();
            
            // 삭제 전에 해당 rental_id의 정보 확인
            String selectSql = "SELECT camper_id FROM rental WHERE rental_id = ?";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, rentalId);
            rs = pstmt.executeQuery();
            
            int camperId = -1;
            if (rs.next()) {
                camperId = rs.getInt("camper_id");
            }
            
            rs.close();
            pstmt.close();
            
            // 대여 삭제 수행
            String deleteSql = "DELETE FROM rental WHERE rental_id = ?";
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setInt(1, rentalId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 대여 삭제 완료 (rental_id: " + rentalId + 
                                   ", camper_id: " + camperId + ")");
                
                // 삭제 후 해당 캠핑카의 대여 상태 확인
                pstmt.close();
                String countSql = "SELECT COUNT(*) as count FROM rental WHERE camper_id = ?";
                pstmt = conn.prepareStatement(countSql);
                pstmt.setInt(1, camperId);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("   👉 캠핑카 " + camperId + "의 남은 대여 수: " + count);
                }
            } else {
                System.out.println("❌ 해당 rental_id 없음 (삭제 실패)");
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 삭제 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}

