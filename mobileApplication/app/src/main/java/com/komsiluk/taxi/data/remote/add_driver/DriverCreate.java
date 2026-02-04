package com.komsiluk.taxi.data.remote.add_driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.komsiluk.taxi.data.remote.add_driver.VehicleCreate;

public class DriverCreate {

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("profileImageUrl")
    @Expose
    private String profileImageUrl;

    @SerializedName("vehicle")
    @Expose
    private VehicleCreate vehicle;

    public DriverCreate() {}

    public DriverCreate(String firstName, String lastName, String address, String city,
                        String phoneNumber, String email, VehicleCreate vehicle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vehicle = vehicle;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public VehicleCreate getVehicle() { return vehicle; }
    public void setVehicle(VehicleCreate vehicle) { this.vehicle = vehicle; }
}
