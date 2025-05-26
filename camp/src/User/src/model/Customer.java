package model;

public class Customer {
    private int customerId;
    private String username;
    private String password;
    private String licenseNumber;
    private String customerName;
    private String address;
    private String phone;
    private String email;
    private java.sql.Date previousRentalDate;
    private String previousCamperType;

    // Getter & Setter
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public java.sql.Date getPreviousRentalDate() {
        return previousRentalDate;
    }
    public void setPreviousRentalDate(java.sql.Date previousRentalDate) {
        this.previousRentalDate = previousRentalDate;
    }

    public String getPreviousCamperType() {
        return previousCamperType;
    }
    public void setPreviousCamperType(String previousCamperType) {
        this.previousCamperType = previousCamperType;
    }
}
