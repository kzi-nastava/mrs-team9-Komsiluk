package rs.ac.uns.ftn.iss.Komsiluk.dtos.rating;

public class RatingCreateDTO {

    private Long raterId;
    private Integer vehicleGrade;
    private Integer driverGrade;
    private String comment;

    public RatingCreateDTO() {
        super();
    }

    public Long getRaterId() {
        return raterId;
    }

    public void setRaterId(Long raterId) {
        this.raterId = raterId;
    }

    public Integer getVehicleGrade() {
        return vehicleGrade;
    }

    public void setVehicleGrade(Integer vehicleGrade) {
        this.vehicleGrade = vehicleGrade;
    }

    public Integer getDriverGrade() {
        return driverGrade;
    }

    public void setDriverGrade(Integer driverGrade) {
        this.driverGrade = driverGrade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
