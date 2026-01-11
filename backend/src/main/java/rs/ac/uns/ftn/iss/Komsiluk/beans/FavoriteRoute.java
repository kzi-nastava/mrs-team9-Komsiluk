package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

@Entity
@Table(name = "favorite_routes")
public class FavoriteRoute {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
    private String title;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "route_id", nullable = false)
    private Route route;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "favorite_route_passengers", joinColumns = @JoinColumn(name = "favorite_route_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> passengers;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private VehicleType vehicleType;
	
	@Column(nullable = false)
    private boolean petFriendly;
	
	@Column(nullable = false)
    private boolean babyFriendly;

    public FavoriteRoute() {
		super();
	}
    
    	public FavoriteRoute(Long id, String title, Route route, User user, List<User> passengers, VehicleType vehicleType, boolean petFriendly, boolean babyFriendly) {
		super();
		this.id = id;
		this.title = title;
		this.route = route;
		this.user = user;
		this.passengers = passengers;
		this.vehicleType = vehicleType;
		this.petFriendly = petFriendly;
		this.babyFriendly = babyFriendly;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<User> getPassengers() {
		return passengers;
	}
	
	public void setPassengers(List<User> passengers) {
		this.passengers = passengers;
	}
	
	public VehicleType getVehicleType() {
		return vehicleType;
	}
	
	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	public boolean isPetFriendly() {
		return petFriendly;
	}
	
	public void setPetFriendly(boolean petFriendly) {
		this.petFriendly = petFriendly;
	}
	
	public boolean isBabyFriendly() {
		return babyFriendly;
	}
	
	public void setBabyFriendly(boolean babyFriendly) {
		this.babyFriendly = babyFriendly;
	}
}
