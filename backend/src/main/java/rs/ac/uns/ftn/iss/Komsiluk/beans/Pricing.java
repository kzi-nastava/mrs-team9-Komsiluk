package rs.ac.uns.ftn.iss.Komsiluk.beans;

import jakarta.persistence.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

import java.time.LocalDateTime;

@Entity
@Table(name = "pricing", uniqueConstraints = @UniqueConstraint(columnNames = "vehicle_type"))
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="vehicle_type", nullable=false)
    private VehicleType vehicleType;

    @Column(name="starting_price", nullable=false, precision=10, scale=2)
    private Integer startingPrice;

    @Column(name="price_per_km", nullable=false, precision=10, scale=2)
    private Integer pricePerKm;

    private LocalDateTime updatedAt;

    @PrePersist @PreUpdate
    void touch() { updatedAt = LocalDateTime.now(); }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Integer startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Integer getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Integer pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

