package rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency;

public class InconsistencyReportCreateDTO {

    private Long passengerId;
    private String message;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
