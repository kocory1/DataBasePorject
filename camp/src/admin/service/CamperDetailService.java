package admin.service;

import admin.model.*;
import common.DBConnect;
import java.sql.*;
import java.util.*;

/**
 * 캠핑카 상세 조회 서비스
 * 캠핑카 선택 시 자체/외부 정비 내역, 부품 정보 등을 조회
 */
public class CamperDetailService {
    private Connection connection;
    
    public CamperDetailService(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * 캠핑카 기본 정보 조회
     */
    public CamperDetailInfo getCamperDetail(int camperId) throws SQLException {
        CamperDetailInfo detail = new CamperDetailInfo();
        
        // 캠핑카 기본 정보 (ini.sql 스키마에 맞게 수정)
        String sql = """
            SELECT c.*, rc.name as company_name, rc.address as company_address,
                   rc.phone as company_phone, rc.manager_name, rc.manager_email
            FROM Camper c 
            JOIN RentalCompany rc ON c.rental_company_id = rc.rental_company_id 
            WHERE c.camper_id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camperId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    detail.setCamperId(rs.getInt("camper_id"));
                    detail.setName(rs.getString("name"));
                    detail.setVehicleNumber(rs.getString("vehicle_number"));
                    detail.setSeats(rs.getInt("seats"));
                    detail.setImageUrl(rs.getString("image_url"));
                    detail.setDetails(rs.getString("details"));
                    detail.setRentalFee(rs.getBigDecimal("rental_fee"));
                    detail.setRegistrationDate(rs.getDate("registration_date"));
                    detail.setRentalCompanyId(rs.getInt("rental_company_id"));
                    detail.setCompanyName(rs.getString("company_name"));
                    detail.setCompanyAddress(rs.getString("company_address"));
                    detail.setCompanyPhone(rs.getString("company_phone"));
                    detail.setManagerName(rs.getString("manager_name"));
                    detail.setManagerEmail(rs.getString("manager_email"));
                }
            }
        }
        
        // 자체 정비 내역
        detail.setInternalMaintenanceList(getInternalMaintenance(camperId));
        
        // 외부 정비 내역
        detail.setExternalMaintenanceList(getExternalMaintenance(camperId));
        
        return detail;
    }
    
    /**
     * 자체 정비 내역 조회
     */
    private List<InternalMaintenanceInfo> getInternalMaintenance(int camperId) throws SQLException {
        List<InternalMaintenanceInfo> maintenanceList = new ArrayList<>();
        
        String sql = """
            SELECT im.*, p.part_name, p.part_price, p.stock_quantity, p.entry_date, p.supplier_name, 
                   e.employee_name, e.department_name, e.role
            FROM InternalMaintenance im 
            JOIN Part p ON im.part_id = p.part_id 
            JOIN Employee e ON im.employee_id = e.employee_id 
            WHERE im.camper_id = ?
            ORDER BY im.maintenance_date DESC
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camperId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InternalMaintenanceInfo info = new InternalMaintenanceInfo();
                    info.setInternalMaintenanceId(rs.getInt("internal_maintenance_id"));
                    info.setCamperId(rs.getInt("camper_id"));
                    info.setPartId(rs.getInt("part_id"));
                    info.setMaintenanceDate(rs.getDate("maintenance_date"));
                    info.setMaintenanceDurationMinutes(rs.getInt("maintenance_duration_minutes"));
                    info.setEmployeeId(rs.getInt("employee_id"));
                    info.setPartName(rs.getString("part_name"));
                    info.setPartPrice(rs.getBigDecimal("part_price"));
                    info.setStockQuantity(rs.getInt("stock_quantity"));
                    info.setEntryDate(rs.getDate("entry_date"));
                    info.setSupplierName(rs.getString("supplier_name"));
                    info.setEmployeeName(rs.getString("employee_name"));
                    info.setDepartmentName(rs.getString("department_name"));
                    info.setRole(rs.getString("role"));
                    maintenanceList.add(info);
                }
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * 외부 정비 내역 조회
     */
    private List<ExternalMaintenanceInfo> getExternalMaintenance(int camperId) throws SQLException {
        List<ExternalMaintenanceInfo> maintenanceList = new ArrayList<>();
        
        String sql = """
            SELECT em.*, ems.shop_name, ems.shop_address, ems.shop_phone, ems.manager_name, ems.manager_email,
                   c.customer_name
            FROM ExternalMaintenance em 
            JOIN ExternalMaintenanceShop ems ON em.shop_id = ems.shop_id 
            JOIN Customer c ON em.license_number = c.license_number
            WHERE em.camper_id = ?
            ORDER BY em.repair_date DESC
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camperId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ExternalMaintenanceInfo info = new ExternalMaintenanceInfo();
                    info.setExternalMaintenanceId(rs.getInt("external_maintenance_id"));
                    info.setCamperId(rs.getInt("camper_id"));
                    info.setShopId(rs.getInt("shop_id"));
                    info.setRentalCompanyId(rs.getInt("rental_company_id"));
                    info.setLicenseNumber(rs.getString("license_number"));
                    info.setMaintenanceDetails(rs.getString("maintenance_details"));
                    info.setRepairDate(rs.getDate("repair_date"));
                    info.setRepairCost(rs.getBigDecimal("repair_cost"));
                    info.setPaymentDueDate(rs.getDate("payment_due_date"));
                    info.setAdditionalMaintenanceDetails(rs.getString("additional_maintenance_details"));
                    info.setShopName(rs.getString("shop_name"));
                    info.setShopAddress(rs.getString("shop_address"));
                    info.setShopPhone(rs.getString("shop_phone"));
                    info.setManagerName(rs.getString("manager_name"));
                    info.setManagerEmail(rs.getString("manager_email"));
                    info.setCustomerName(rs.getString("customer_name"));
                    maintenanceList.add(info);
                }
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * 모든 캠핑카 목록 조회 (선택용)
     */
    public List<CamperSummary> getAllCampers() throws SQLException {
        List<CamperSummary> campers = new ArrayList<>();
        
        String sql = """
            SELECT c.camper_id, c.name, c.vehicle_number, rc.name as company_name
            FROM Camper c 
            JOIN RentalCompany rc ON c.rental_company_id = rc.rental_company_id 
            ORDER BY c.camper_id
            """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CamperSummary summary = new CamperSummary();
                summary.setCamperId(rs.getInt("camper_id"));
                summary.setName(rs.getString("name"));  // setCamperName -> setName 변경
                summary.setVehicleNumber(rs.getString("vehicle_number"));
                summary.setCompanyName(rs.getString("company_name"));
                campers.add(summary);
            }
        }
        
        return campers;
    }
}
