package User.dao_user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import common.DBConnect;
import User.model.Camper;
import User.model.Period;

/**
 * CamperDAO
 *
 * 1) List<Camper> getAllCampers()
 *    → CustomerMainView / RentalDialog에서 “캠핑카 전체 조회” 용
 *
 * 2) List<Period> getRentalPeriodsForCamper(int camperId)
 *    → CustomerMainView의 예약 기간 테이블과 RentalDialog에서 “이미 예약된 기간” 테이블을 채우기 위해 사용
 *
 * 3) List<Camper> getAvailableCampers(Date startDate, int period)
 *    → (추가 기능) 특정 기간에 대여 가능한 캠핑카만 조회하고 싶을 때 사용
 */
public class CamperDAO {

    /**
     * 1) 캠핑카 전체 조회
     */
    public List<Camper> getAllCampers() throws SQLException {
        List<Camper> list = new ArrayList<>();
        String sql = "SELECT camper_id, name, vehicle_number, seats, image_url, details, rental_fee, rental_company_id, registration_date FROM Camper";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                Camper c = new Camper();
                c.setCamperId(rs.getInt("camper_id"));
                c.setName(rs.getString("name"));
                c.setVehicleNumber(rs.getString("vehicle_number"));
                c.setSeats(rs.getInt("seats"));
                c.setImageUrl(rs.getString("image_url"));
                c.setDetails(rs.getString("details"));
                c.setRentalFee(rs.getDouble("rental_fee"));
                c.setRentalCompanyId(rs.getInt("rental_company_id"));
                c.setRegistrationDate(rs.getDate("registration_date"));
                list.add(c);
            }
        }
        return list;
    }

    /**
     * 2) 특정 캠핑카ID가 예약된 모든 Period(시작일~만기일) 조회
     *    → 내부적으로 RentalDAO.getRentalPeriodsForCamper(int) 호출 가능
     */
    public List<Period> getRentalPeriodsForCamper(int camperId) throws SQLException {
        RentalDAO rDao = new RentalDAO();
        return rDao.getRentalPeriodsForCamper(camperId);
    }

    /**
     * 3) 특정 기간(startDate, period)에 예약되지 않은 캠핑카(Available) 조회
     *    → (필요 시) “기간으로 캠핑카 검색” 기능 구현 때 사용
     */
    public List<Camper> getAvailableCampers(Date startDate, int period) throws SQLException {
        List<Camper> list = new ArrayList<>();
        String sql = ""
            + "SELECT * FROM Camper WHERE camper_id NOT IN ("
            + "  SELECT camper_id FROM Rental WHERE NOT ("
            + "    DATE_ADD(rental_start_date, INTERVAL rental_period DAY) < ? "
            + "    OR rental_start_date > DATE_ADD(?, INTERVAL ? DAY)"
            + "  )"
            + ")";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, startDate);
            pstmt.setInt(3, period);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Camper c = new Camper();
                    c.setCamperId(rs.getInt("camper_id"));
                    c.setName(rs.getString("name"));
                    c.setVehicleNumber(rs.getString("vehicle_number"));
                    c.setSeats(rs.getInt("seats"));
                    c.setImageUrl(rs.getString("image_url"));
                    c.setDetails(rs.getString("details"));
                    c.setRentalFee(rs.getDouble("rental_fee"));
                    c.setRentalCompanyId(rs.getInt("rental_company_id"));
                    c.setRegistrationDate(rs.getDate("registration_date"));
                    list.add(c);
                }
            }
        }
        return list;
    }
    
    
    /**
     * 3) (새로 추가) 해당 캠핑카가 현재 “예약 중”인지 여부를 간단히 조회
     *    = 예약된 Period(리스트)가 하나도 없으면 “예약 가능”으로 간주
     */
    public boolean isAvailable(int camperId) throws SQLException {
        List<Period> booked = getRentalPeriodsForCamper(camperId);
        return booked.isEmpty();
    }

    /**
     * 4) 특정 ID로 캠핑카 정보 조회
     */
    public Camper getCamperById(int camperId) throws SQLException {
        String sql = "SELECT camper_id, name, vehicle_number, seats, image_url, details, rental_fee, rental_company_id, registration_date FROM Camper WHERE camper_id = ?";
        
        try (Connection conn = DBConnect.getUserConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, camperId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Camper c = new Camper();
                    c.setCamperId(rs.getInt("camper_id"));
                    c.setName(rs.getString("name"));
                    c.setVehicleNumber(rs.getString("vehicle_number"));
                    c.setSeats(rs.getInt("seats"));
                    c.setImageUrl(rs.getString("image_url"));
                    c.setDetails(rs.getString("details"));
                    c.setRentalFee(rs.getDouble("rental_fee"));
                    c.setRentalCompanyId(rs.getInt("rental_company_id"));
                    c.setRegistrationDate(rs.getDate("registration_date"));
                    return c;
                }
            }
        }
        return null; // 해당 ID의 캠핑카가 없으면 null 반환
    }

}
