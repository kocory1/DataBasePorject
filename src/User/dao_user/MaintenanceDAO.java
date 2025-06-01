package User.dao_user;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import common.DBConnect;
import User.model.MaintenanceRecord;

/**
 * MaintenanceDAO
 *
 * 1) ArrayList<MaintenanceRecord> getInternalMaintenance(int camperId)
 *    → 내부 정비 이력 조회
 * 2) ArrayList<MaintenanceRecord> getExternalMaintenance(int camperId)
 *    → 외부 정비 이력 조회
 * 3) ArrayList<String[]> getAllShops()
 *    → ExternalMaintenanceShop 테이블 전체 조회 (MaintenanceRequestDialog의 콤보박스 채우기)
 * 4) boolean insertExternalMaintenance(int camperId, int shopId, String licenseNumber,
 *                                      String details, Date repairDate,
 *                                      double estimatedCost, String additionalDetails)
 *    → MaintenanceRequestDialog에서 “정비 요청” 시 호출
 */
public class MaintenanceDAO {

    /**
     * 1) 내부 정비 이력 조회
     *    ⇒ internalmaintenance 테이블에서 camper_id가 일치하는 레코드 모두 가져옴
     *    → MaintenanceRequestDialog 이후에도 CustomerMainView에서 “정비 정보 로드” 시 필요할 수 있음
     */
    public ArrayList<MaintenanceRecord> getInternalMaintenance(int camperId) {
        ArrayList<MaintenanceRecord> list = new ArrayList<>();
        String sql = ""
            + "SELECT maintenance_date, maintenance_duration_minutes "
            + "FROM internalmaintenance "
            + "WHERE camper_id = ?";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, camperId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MaintenanceRecord record = new MaintenanceRecord();
                    record.setType("내부");
                    record.setMaintenanceDate(rs.getDate("maintenance_date"));
                    record.setDurationMinutes(rs.getInt("maintenance_duration_minutes"));
                    record.setDetails("내부 정비 기록"); // 상세 내용이 테이블에 없으면 고정 텍스트로 표시
                    list.add(record);
                }
            }
        } catch (Exception e) {
            System.out.println("내부 정비 조회 오류: " + e.getMessage());
        }
        return list;
    }

    /**
     * 2) 외부 정비 이력 조회
     *    ⇒ externalmaintenance 테이블에서 camper_id가 일치하는 레코드 가져옴
     *    → shop_id를 통해 shop 이름을 추가로 가져와서 MaintenanceRecord.setShopName(...) 으로 세팅
     */
    public ArrayList<MaintenanceRecord> getExternalMaintenance(int camperId) {
        ArrayList<MaintenanceRecord> list = new ArrayList<>();
        String sql = ""
            + "SELECT maintenance_details, repair_date, repair_cost, shop_id, additional_maintenance_details "
            + "FROM externalmaintenance "
            + "WHERE camper_id = ?";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, camperId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MaintenanceRecord record = new MaintenanceRecord();
                    record.setType("외부");
                    record.setMaintenanceDate(rs.getDate("repair_date"));
                    record.setDetails(rs.getString("maintenance_details"));
                    record.setCost(rs.getDouble("repair_cost"));
                    record.setAdditionalDetails(rs.getString("additional_maintenance_details"));

                    // shop_id → shop_name 조회
                    int shopId = rs.getInt("shop_id");
                    String shopName = getShopNameById(shopId, conn);
                    record.setShopName(shopName);

                    list.add(record);
                }
            }
        } catch (Exception e) {
            System.out.println("외부 정비 조회 오류: " + e.getMessage());
        }
        return list;
    }

    /**
     * 3) 모든 정비소(ExternalMaintenanceShop) 목록 조회
     *    → MaintenanceRequestDialog의 콤보박스에 shop_name을 표시하고,
     *      내부적으로는 shop_id를 이용하기 위해 [shop_id, shop_name] 형태의 String[] 리스트 반환
     */
    public ArrayList<String[]> getAllShops() {
        ArrayList<String[]> shopList = new ArrayList<>();
        String sql = "SELECT shop_id, shop_name FROM externalmaintenanceshop ORDER BY shop_name";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                String[] shop = new String[2];
                shop[0] = String.valueOf(rs.getInt("shop_id"));   // 콤보박스 내부에서는 String으로도 처리
                shop[1] = rs.getString("shop_name");
                shopList.add(shop);
            }
        } catch (Exception e) {
            System.out.println("정비소 목록 조회 오류: " + e.getMessage());
        }
        return shopList;
    }

    /**
     * 4) 외부 정비 요청 등록
     *    → insertExternalMaintenance(...) 호출 시, 
     *       GET MAX(external_maintenance_id)+1 → 새로운 ID 생성 → INSERT 수행
     *    → 납입기한(payment_due_date)은 repairDate로부터 7일 후로 설정
     *
     * @param camperId           정비 요청할 캠핑카 ID
     * @param shopId             외부 정비소 ID
     * @param licenseNumber      로그인된 회원의 운전면허번호
     * @param details            정비 세부 내용
     * @param repairDate         수리(정비) 예정일 (java.util.Date)
     * @param estimatedCost      예상 비용
     * @param additionalDetails  기타 정비 내역 (추가 설명)
     *
     * @return true: 등록 성공, false: 등록 실패
     */
    public boolean insertExternalMaintenance(int camperId,
                                             int shopId,
                                             String licenseNumber,
                                             String details,
                                             Date repairDate,
                                             double estimatedCost,
                                             String additionalDetails) {
        boolean success = false;

        // 1) 새로운 external_maintenance_id 생성 (max+1)
        int newMaintenanceId = 1;
        String idSql = "SELECT COALESCE(MAX(external_maintenance_id), 0) + 1 AS next_id FROM externalmaintenance";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt1 = conn.prepareStatement(idSql);
            ResultSet rs = pstmt1.executeQuery()
        ) {
            if (rs.next()) {
                newMaintenanceId = rs.getInt("next_id");
            }
        } catch (Exception e) {
            System.out.println("ID 생성 오류: " + e.getMessage());
            return false;
        }

        // 2) INSERT
        String sql = ""
            + "INSERT INTO externalmaintenance ("
            + "  external_maintenance_id, camper_id, shop_id, rental_company_id, license_number, "
            + "  maintenance_details, repair_date, repair_cost, payment_due_date, additional_maintenance_details"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnect.getUserConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 2-1) 렌탈 회사 ID 조회 (Insert 시 필요)
            int rentalCompanyId = getRentalCompanyId(camperId, conn);

            // 2-2) repairDate → java.sql.Date 로 변환
            java.sql.Date sqlRepairDate = new java.sql.Date(repairDate.getTime());
            // 납입기한: repairDate + 7일
            long millis = sqlRepairDate.getTime() + (7L * 24 * 60 * 60 * 1000);
            java.sql.Date paymentDueDate = new java.sql.Date(millis);

            pstmt.setInt(1, newMaintenanceId);
            pstmt.setInt(2, camperId);
            pstmt.setInt(3, shopId);
            pstmt.setInt(4, rentalCompanyId);
            pstmt.setString(5, licenseNumber);
            pstmt.setString(6, details);
            pstmt.setDate(7, sqlRepairDate);
            pstmt.setDouble(8, estimatedCost);
            pstmt.setDate(9, paymentDueDate);
            pstmt.setString(10, additionalDetails != null ? additionalDetails : "");

            int rows = pstmt.executeUpdate();
            success = rows > 0;
        } catch (Exception e) {
            System.out.println("외부 정비 요청 등록 오류: " + e.getMessage());
        }
        return success;
    }

    /**
     * shop_id → shop_name 조회 헬퍼 메서드
     */
    private String getShopNameById(int shopId, Connection conn) {
        String name = "";
        String sql = "SELECT shop_name FROM externalmaintenanceshop WHERE shop_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, shopId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("shop_name");
                }
            }
        } catch (Exception e) {
            System.out.println("정비소 이름 조회 오류: " + e.getMessage());
        }
        return name;
    }

    /**
     *  → camper_id → rental_company_id 조회
     *    (MaintenanceRequestDialog에서 insertExternalMaintenance 호출 전에, 렌탈 회사 ID가 필요하므로 사용)
     */
    private int getRentalCompanyId(int camperId, Connection conn) {
        int rentalCompanyId = 1;
        String sql = "SELECT rental_company_id FROM camper WHERE camper_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, camperId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rentalCompanyId = rs.getInt("rental_company_id");
                }
            }
        } catch (Exception e) {
            System.out.println("렌탈 회사 ID 조회 오류: " + e.getMessage());
        }
        return rentalCompanyId;
    }
}
