package User.dao_user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import common.DBConnect;
import User.model.Rental;
import User.model.Period;

/**
 * RentalDAO
 *
 * 1) List<Period> getRentalPeriodsForCamper(int camperId)
 *    - RentalDialog, CustomerMainView 등에서 “예약된 기간”을 조회할 때 사용
 *
 * 2) boolean isOverlapping(int camperId, Date startDate, int period)
 *    - RentalDialog, ModifyRentalDialog 에서 종종 “중복 예약 여부”를 체크할 때 사용
 *
 * 3) void insertRental(Rental r)
 *    - RentalDialog에서 “등록” 버튼 클릭 시 호출
 *
 * 4) ArrayList<Rental> getRentalsByLicense(String licenseNumber)
 *    - CustomerMainView에서 “내 대여 내역 로드”할 때, ModifyRentalDialog에서도 사용
 *
 * 5) boolean deleteRental(int rentalId)
 *    - CustomerMainView에서 “대여 취소” 버튼 클릭 시 호출 (License 검증은 View 단에서 이미 로그인된 회원만 삭제 가능하다고 가정)
 *
 * 6) boolean updateRentalDate(int rentalId, String licenseNumber, Date newStartDate, int newPeriod, Date newPaymentDueDate)
 *    - ModifyRentalDialog의 “일정 변경” 탭에서 호출
 *
 * 7) boolean updateRentalCamper(int rentalId, String licenseNumber, int newCamperId)
 *    - ModifyRentalDialog의 “캠핑카 변경” 탭에서 호출
 */
public class RentalDAO {

    /**
     * 1) 특정 캠핑카(camperId)에 대해, 현재까지 예약된 모든 시작일~만기일을 Period 리스트로 반환
     *    ※ CustomerMainView, RentalDialog 등에서 “예약된 기간” 테이블을 채우기 위해 호출됨
     */
    public List<Period> getRentalPeriodsForCamper(int camperId) throws SQLException {
        List<Period> periods = new ArrayList<>();
        String sql = ""
            + "SELECT rental_start_date, "
            + "       DATE_ADD(rental_start_date, INTERVAL rental_period DAY) AS end_date "
            + "FROM Rental "
            + "WHERE camper_id = ? "
            + "ORDER BY rental_start_date";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, camperId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date start = rs.getDate("rental_start_date");
                    Date end   = rs.getDate("end_date");
                    periods.add(new Period(start, end));
                }
            }
        }
        return periods;
    }

    /**
     * 2) 특정 캠핑카ID와 요청 기간(startDate, period)이 겹치는 예약이 있는지 확인
     *    → RentalDialog, ModifyRentalDialog에서 “중복 검사” 시 호출됨
     */
    public boolean isOverlapping(int camperId, Date startDate, int period) throws SQLException {
        String sql = ""
            + "SELECT COUNT(*) AS cnt "
            + "FROM Rental "
            + "WHERE camper_id = ? "
            + "  AND NOT ( DATE_ADD(rental_start_date, INTERVAL rental_period DAY) < ? "
            + "            OR rental_start_date > DATE_ADD(?, INTERVAL ? DAY) )";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, camperId);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, startDate);
            pstmt.setInt(4, period);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        }
        return false;
    }

    /**
     * 3) 새로운 Rental 레코드를 삽입
     *    → RentalDialog의 “등록” 버튼 클릭 시 사용
     *    → r 객체에 setRentalId(), setCamperId(), setLicenseNumber(), setRentalCompanyId(),
     *       setRentalStartDate(), setRentalPeriod(), setBillAmount(), setPaymentDueDate(),
     *       setAdditionalChargesDescription(""), setAdditionalChargesAmount(0.0) 등을 미리 세팅한 뒤 호출
     */
    public void insertRental(Rental r) throws SQLException {
        String sql = ""
            + "INSERT INTO Rental ("
            + "  rental_id, camper_id, license_number, rental_company_id, "
            + "  rental_start_date, rental_period, bill_amount, payment_due_date, "
            + "  additional_charges_description, additional_charges_amount"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, r.getRentalId());
            pstmt.setInt(2, r.getCamperId());
            pstmt.setString(3, r.getLicenseNumber());
            pstmt.setInt(4, r.getRentalCompanyId());
            pstmt.setDate(5, r.getRentalStartDate());
            pstmt.setInt(6, r.getRentalPeriod());
            pstmt.setDouble(7, r.getBillAmount());
            pstmt.setDate(8, r.getPaymentDueDate());
            pstmt.setString(9, r.getAdditionalChargesDescription());
            pstmt.setDouble(10, r.getAdditionalChargesAmount());

            pstmt.executeUpdate();
        }
    }

    /**
     * 4) 특정 운전면허번호(licenseNumber)에 해당하는 모든 Rental 레코드를 ArrayList<Rental> 형태로 반환
     *    → CustomerMainView에서 “내 대여 내역 로드” 시 사용
     *    → ModifyRentalDialog에서도 getRentalsByLicense(license) 후 ID 매칭용으로 사용
     */
    public ArrayList<Rental> getRentalsByLicense(String licenseNumber) throws SQLException {
        ArrayList<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM Rental WHERE license_number = ? ORDER BY rental_start_date DESC";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, licenseNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        }
        return list;
    }

    /**
     * 5) 특정 rentalId에 해당하는 레코드를 삭제
     *    → CustomerMainView에서 “대여 취소” 시 호출 (license 체크를 따로 하지 않음)
     *
     * @param rentalId 삭제할 대여 ID
     * @return true: 삭제 성공, false: 삭제 실패
     */
    public boolean deleteRental(int rentalId) throws SQLException {
        String sql = "DELETE FROM Rental WHERE rental_id = ?";
        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, rentalId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * 6) 대여 일정(시작일, 기간, 만기일) 수정
     *    → ModifyRentalDialog의 일정 변경 탭에서 호출
     *
     * @param rentalId         수정할 대여 ID
     * @param licenseNumber    (추가 보안 체크용) 로그인된 회원의 운전면허번호
     * @param newStartDate     새로운 대여 시작일
     * @param newPeriod        새로운 대여 기간(일수)
     * @param newPaymentDueDate 새로운 납입기한(만기일)
     * @return true: 수정 성공, false: 수정 실패
     */
    public boolean updateRentalDate(int rentalId, String licenseNumber,
                                    Date newStartDate, int newPeriod, Date newPaymentDueDate) throws SQLException {
        String sql = ""
            + "UPDATE Rental "
            + "   SET rental_start_date = ?, "
            + "       rental_period     = ?, "
            + "       payment_due_date  = ? "
            + " WHERE rental_id = ? "
            + "   AND license_number = ?";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setDate(1, newStartDate);
            pstmt.setInt(2, newPeriod);
            pstmt.setDate(3, newPaymentDueDate);
            pstmt.setInt(4, rentalId);
            pstmt.setString(5, licenseNumber);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * 7) 대여 캠핑카만 변경
     *    → ModifyRentalDialog의 캠핑카 변경 탭에서 호출
     *
     * @param rentalId      수정할 대여 ID
     * @param licenseNumber 로그인된 회원의 운전면허번호
     * @param newCamperId   새로운 캠핑카 ID
     * @return true: 수정 성공, false: 수정 실패
     */
    public boolean updateRentalCamper(int rentalId, String licenseNumber, int newCamperId) throws SQLException {
        String sql = ""
            + "UPDATE Rental "
            + "   SET camper_id = ? "
            + " WHERE rental_id = ? "
            + "   AND license_number = ?";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, newCamperId);
            pstmt.setInt(2, rentalId);
            pstmt.setString(3, licenseNumber);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }
}
