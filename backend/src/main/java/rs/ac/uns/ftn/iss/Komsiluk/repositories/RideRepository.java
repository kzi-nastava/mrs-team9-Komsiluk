package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

	@Query("""
	        SELECT r
	        FROM Ride r
	        WHERE r.status = :status
	          AND r.createdBy.id = :userId
	        """)
	Collection<Ride> findScheduledByUserId(@Param("userId") Long userId, @Param("status") RideStatus status);

    Collection<Ride> findByDriverIdAndStatusOrderByCreatedAtDesc(Long driverId, RideStatus status);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime from);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime to);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime from, LocalDateTime to);

}
