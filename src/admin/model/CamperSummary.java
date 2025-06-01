package admin.model;

/**
 * 캠핑카 요약 정보 DTO (선택용) - ini.sql 스키마에 맞게 수정
 */
public class CamperSummary {
    private int camperId;
    private String name;  // camperName -> name 변경 (ini.sql 스키마에 맞춰)
    private String vehicleNumber;
    private String companyName;
    
    // Getters and Setters
    public int getCamperId() { return camperId; }
    public void setCamperId(int camperId) { this.camperId = camperId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // 호환성을 위한 메서드들 (기존 코드와의 호환)
    public String getCamperName() { return name; }
    public void setCamperName(String camperName) { this.name = camperName; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", 
            camperId, name, vehicleNumber, companyName);
    }
}
