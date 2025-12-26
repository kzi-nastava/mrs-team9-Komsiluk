package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

public class DriverActivityPeriod {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private User driver;
    
    public DriverActivityPeriod() {
		super();
	}
    
    	public DriverActivityPeriod(Long id, LocalDateTime startTime, LocalDateTime endTime, User driver) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.driver = driver;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}
    	
}
