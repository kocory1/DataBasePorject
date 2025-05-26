package model;

import java.util.Date;

public class MaintenanceRecord {
    private String type; // "내부" 또는 "외부"
    private Date maintenanceDate;
    private String details;
    private int durationMinutes; // 내부정비용
    private String shopName;     // 외부정비용
    private double cost;         // 외부정비용

    // Getter/Setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Date getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(Date maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
