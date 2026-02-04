package com.komsiluk.taxi.data.remote.add_driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.komsiluk.taxi.data.remote.profile.VehicleResponse;

public class DriverResponse {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("email")
    @Expose
    private String email;

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

    @SerializedName("profileImageUrl")
    @Expose
    private String profileImageUrl;

    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("blocked")
    @Expose
    private boolean blocked;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("role")
    @Expose
    private String role;

    @SerializedName("driverStatus")
    @Expose
    private String driverStatus;

    @SerializedName("vehicle")
    @Expose
    private VehicleResponse vehicle;

    public DriverResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public VehicleResponse getVehicle() { return vehicle; }
    public void setVehicle(VehicleResponse vehicle) { this.vehicle = vehicle; }
}