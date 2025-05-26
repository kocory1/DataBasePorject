package dao_user;

import dbConnect.Db1;
import model.Rental;

import java.sql.*;
import java.util.ArrayList;

public class RentalDAO {

    // ✅ 1. 대여 등록
    public void insertRental(Rental rental) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Db1.getUserConnection();
            String sql = "INSERT INTO rental (camper_id, license_number, rental_company_id, rental_start_date, rental_period, bill_amount, payment_due_date, additional_charges_description, additional_charges_amount) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rental.getCamperId());
            pstmt.setString(2, rental.getLicenseNumber());
            pstmt.setInt(3, rental.getRentalCompanyId());
            pstmt.setDate(4, rental.getRentalStartDate());
            pstmt.setInt(5, rental.getRentalPeriod());
            pstmt.setDouble(6, rental.getBillAmount());
            pstmt.setDate(7, rental.getPaymentDueDate());
            pstmt.setString(8, rental.getAdditionalChargesDescription());
            pstmt.setDouble(9, rental.getAdditionalChargesAmount());

            pstmt.executeUpdate();
            System.out.println("✅ 대여 등록 완료");

        } catch (Exception e) {
            System.out.println("❌ 대여 등록 오류: " + e.getMessage());
        } finally {
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
            conn = Db1.getUserConnection();
            String sql = "SELECT * FROM rental WHERE license_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, licenseNumber);
            rs = pstmt.executeQuery();

            while (rs.next()) {
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
                list.add(r);
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 조회 오류: " + e.getMessage());
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
            conn = Db1.getUserConnection();
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

        try {
            conn = Db1.getUserConnection();
            String sql = "DELETE FROM rental WHERE rental_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rentalId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 대여 삭제 완료");
            } else {
                System.out.println("❌ 해당 rental_id 없음 (삭제 실패)");
            }

        } catch (Exception e) {
            System.out.println("❌ 대여 삭제 오류: " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}

