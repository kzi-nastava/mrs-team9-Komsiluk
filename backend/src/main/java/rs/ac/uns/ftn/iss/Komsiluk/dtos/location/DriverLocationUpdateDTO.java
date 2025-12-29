package rs.ac.uns.ftn.iss.Komsiluk.dtos.location;

public class DriverLocationUpdateDTO {

    private double lat;
    private double lng;

    public DriverLocationUpdateDTO() {
        super();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
