package com.komsiluk.taxi.data.remote.driver_history;
import com.google.gson.annotations.SerializedName;

public class DriverBasic implements java.io.Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    public DriverBasic() {}

    public DriverBasic(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Pomoćna metoda za spajanje imena i prezimena
    public String getFullName() {
        return firstName + " " + lastName;
    }
}