package admin.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * 외부 정비 정보 DTO - ini.sql 스키마에 맞게 수정
 * ExternalMaintenance + ExternalMaintenanceShop + Customer 테이블 JOIN 정보
 */
public class ExternalMaintenanceInfo {
    // ExternalMaintenance 테이블
    private int externalMaintenanceId;
    private int camperId;
    private int shopId;
    private int rentalCompanyId;
    private String licenseNumber;
    private String maintenanceDetails;
    private Date repairDate;
    private BigDecimal repairCost;
    private Date paymentDueDate;
    private String additionalMaintenanceDetails;
    
    // ExternalMaintenanceShop 테이블 (JOIN으로 가져올 정보)
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String managerName;
    private String managerEmail;
    
    // Customer 테이블 (JOIN으로 가져올 정보)
    private String customerName;
    
    // Getters and Setters
    public int getExternalMaintenanceId() { return externalMaintenanceId; }
    public void setExternalMaintenanceId(int externalMaintenanceId) { this.externalMaintenanceId = externalMaintenanceId; }
    
    public int getCamperId() { return camperId; }
    public void setCamperId(int camperId) { this.camperId = camperId; }
    
    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }
    
    public int getRentalCompanyId() { return rentalCompanyId; }
    public void setRentalCompanyId(int rentalCompanyId) { this.rentalCompanyId = rentalCompanyId; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getMaintenanceDetails() { return maintenanceDetails; }
    public void setMaintenanceDetails(String maintenanceDetails) { this.maintenanceDetails = maintenanceDetails; }
    
    public Date getRepairDate() { return repairDate; }
    public void setRepairDate(Date repairDate) { this.repairDate = repairDate; }
    
    public BigDecimal getRepairCost() { return repairCost; }
    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }
    
    public Date getPaymentDueDate() { return paymentDueDate; }
    public void setPaymentDueDate(Date paymentDueDate) { this.paymentDueDate = paymentDueDate; }
    
    public String getAdditionalMaintenanceDetails() { return additionalMaintenanceDetails; }
    public void setAdditionalMaintenanceDetails(String additionalMaintenanceDetails) { this.additionalMaintenanceDetails = additionalMaintenanceDetails; }
    
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    
    public String getShopAddress() { return shopAddress; }
    public void setShopAddress(String shopAddress) { this.shopAddress = shopAddress; }
    
    public String getShopPhone() { return shopPhone; }
    public void setShopPhone(String shopPhone) { this.shopPhone = shopPhone; }
    
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
