package admin.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * 자체 정비 정보 DTO - ini.sql 스키마에 맞게 수정
 * InternalMaintenance + Part + Employee 테이블 JOIN 정보
 */
public class InternalMaintenanceInfo {
    // InternalMaintenance 테이블
    private int internalMaintenanceId;
    private int camperId;
    private int partId;
    private Date maintenanceDate;
    private int maintenanceDurationMinutes;
    private int employeeId;
    
    // Part 테이블 (JOIN으로 가져올 정보)
    private String partName;
    private BigDecimal partPrice;
    private int stockQuantity;
    private Date entryDate;
    private String supplierName;
    
    // Employee 테이블 (JOIN으로 가져올 정보)
    private String employeeName;
    private String departmentName;
    private String role;
    
    // Getters and Setters
    public int getInternalMaintenanceId() { return internalMaintenanceId; }
    public void setInternalMaintenanceId(int internalMaintenanceId) { this.internalMaintenanceId = internalMaintenanceId; }
    
    public int getCamperId() { return camperId; }
    public void setCamperId(int camperId) { this.camperId = camperId; }
    
    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }
    
    public Date getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(Date maintenanceDate) { this.maintenanceDate = maintenanceDate; }
    
    public int getMaintenanceDurationMinutes() { return maintenanceDurationMinutes; }
    public void setMaintenanceDurationMinutes(int maintenanceDurationMinutes) { this.maintenanceDurationMinutes = maintenanceDurationMinutes; }
    
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    
    public BigDecimal getPartPrice() { return partPrice; }
    public void setPartPrice(BigDecimal partPrice) { this.partPrice = partPrice; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
