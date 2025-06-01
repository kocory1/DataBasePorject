package User.model;

import java.sql.Date;

public class Rental {
    private int rentalId;
    private int camperId;
    private String licenseNumber;
    private int rentalCompanyId;
    private Date rentalStartDate;
    private int rentalPeriod;
    private double billAmount;
    private Date paymentDueDate;
    private String additionalChargesDescription;
    private double additionalChargesAmount;

    // Getter & Setter
    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getCamperId() {
        return camperId;
    }

    public void setCamperId(int camperId) {
        this.camperId = camperId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getRentalCompanyId() {
        return rentalCompanyId;
    }

    public void setRentalCompanyId(int rentalCompanyId) {
        this.rentalCompanyId = rentalCompanyId;
    }

    public Date getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(Date rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public int getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(int rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public String getAdditionalChargesDescription() {
        return additionalChargesDescription;
    }

    public void setAdditionalChargesDescription(String additionalChargesDescription) {
        this.additionalChargesDescription = additionalChargesDescription;
    }

    public double getAdditionalChargesAmount() {
        return additionalChargesAmount;
    }

    public void setAdditionalChargesAmount(double additionalChargesAmount) {
        this.additionalChargesAmount = additionalChargesAmount;
    }
}

