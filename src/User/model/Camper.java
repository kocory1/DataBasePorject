package User.model;

import java.sql.Date;

public class Camper {
    private int camperId;
    private String name;
    private String vehicleNumber;
    private int seats;
    private String imageUrl;
    private String details;
    private double rentalFee;
    private int rentalCompanyId;
    private Date registrationDate;

    // Getter & Setter
    public int getCamperId() {
        return camperId;
    }

    public void setCamperId(int camperId) {
        this.camperId = camperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public int getRentalCompanyId() {
        return rentalCompanyId;
    }

    public void setRentalCompanyId(int rentalCompanyId) {
        this.rentalCompanyId = rentalCompanyId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
