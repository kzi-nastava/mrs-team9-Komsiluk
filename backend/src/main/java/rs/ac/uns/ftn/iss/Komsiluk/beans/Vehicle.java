package rs.ac.uns.ftn.iss.Komsiluk.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

@Entity
@Table(name = "vehicles")
public class Vehicle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String model;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VehicleType type;
	
	@Column(nullable = false, unique = true)
	private String licencePlate;
	
	@Column(nullable = false)
	private int seatCount;
	
	@Column(nullable = false)
	private boolean babyFriendly;
	
	@Column(nullable = false)
	private boolean petFriendly;
	
	public Vehicle() {
		super();
	}
	
	public Vehicle(Long id, String model, VehicleType type, String licencePlate, int seatCount, boolean babyFriendly, boolean petFriendly) {
		super();
		this.id = id;
		this.model = model;
		this.type = type;
		this.licencePlate = licencePlate;
		this.seatCount = seatCount;
		this.babyFriendly = babyFriendly;
		this.petFriendly = petFriendly;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public VehicleType getType() {
		return type;
	}
	
	public void setType(VehicleType type) {
		this.type = type;
	}
	
	public String getLicencePlate() {
		return licencePlate;
	}
	
	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}
	
	public int getSeatCount() {
		return seatCount;
	}
	
	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}
	
	public boolean isBabyFriendly() {
		return babyFriendly;
	}
	
	public void setBabyFriendly(boolean babyFriendly) {
		this.babyFriendly = babyFriendly;
	}
	
	public boolean isPetFriendly() {
		return petFriendly;
	}
	
	public void setPetFriendly(boolean petFriendly) {
		this.petFriendly = petFriendly;
	}
}
