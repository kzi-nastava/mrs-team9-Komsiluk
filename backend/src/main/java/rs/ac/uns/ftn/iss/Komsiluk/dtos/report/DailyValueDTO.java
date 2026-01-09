package rs.ac.uns.ftn.iss.Komsiluk.dtos.report;

import java.time.LocalDate;

public class DailyValueDTO {

    private LocalDate date;
    private double value;

    public DailyValueDTO() {
    	super();
    }
    
    public DailyValueDTO(LocalDate date, double value) {
    	super();
		this.date = date;
		this.value = value;
	}

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}