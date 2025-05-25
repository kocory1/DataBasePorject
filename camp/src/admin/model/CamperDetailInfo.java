package admin.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * 캠핑카 상세 정보 DTO - ini.sql 스키마에 맞게 수정
 */
public class CamperDetailInfo {
    // Camper 테이블 필드들
    private int camperId;
    private String name;
    private String vehicleNumber;
    private int seats;
    private String imageUrl;
    private String details;
    private BigDecimal rentalFee;
    private Date registrationDate;
    
    // RentalCompany 테이블 필드들 (JOIN으로 가져올 정보)
    private int rentalCompanyId;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String managerName;
    private String managerEmail;
    
    private List<InternalMaintenanceInfo> internalMaintenanceList;
    private List<ExternalMaintenanceInfo> externalMaintenanceList;
    
    // Getters and Setters
    public int getCamperId() { return camperId; }
    public void setCamperId(int camperId) { this.camperId = camperId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public BigDecimal getRentalFee() { return rentalFee; }
    public void setRentalFee(BigDecimal rentalFee) { this.rentalFee = rentalFee; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public int getRentalCompanyId() { return rentalCompanyId; }
    public void setRentalCompanyId(int rentalCompanyId) { this.rentalCompanyId = rentalCompanyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    
    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }
    
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    
    public List<InternalMaintenanceInfo> getInternalMaintenanceList() { return internalMaintenanceList; }
    public void setInternalMaintenanceList(List<InternalMaintenanceInfo> internalMaintenanceList) { 
        this.internalMaintenanceList = internalMaintenanceList; 
    }
    
    public List<ExternalMaintenanceInfo> getExternalMaintenanceList() { return externalMaintenanceList; }
    public void setExternalMaintenanceList(List<ExternalMaintenanceInfo> externalMaintenanceList) { 
        this.externalMaintenanceList = externalMaintenanceList; 
    }
}
