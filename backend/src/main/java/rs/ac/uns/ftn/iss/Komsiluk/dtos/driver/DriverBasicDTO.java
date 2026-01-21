package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

public class DriverBasicDTO {
    private Long id;
    private String firstName;
    private String lastName;

    public DriverBasicDTO() {}

    public DriverBasicDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
