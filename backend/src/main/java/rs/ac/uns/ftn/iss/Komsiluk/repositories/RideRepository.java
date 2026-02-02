package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

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
          AND (r.createdBy.id = :userId OR r.driver.id = :userId)
        """)
    Collection<Ride> findScheduledByUserId(@Param("userId") Long userId, @Param("status") RideStatus status);
	
	@Query(value = """
		    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
		    FROM rides r
		    WHERE
		        r.created_by = :userId
		        AND r.status IN (:statuses)
		        AND (
		            COALESCE(r.scheduled_at, r.created_at) < :newEnd
		            AND
		            :newStart < (
		                COALESCE(r.scheduled_at, r.created_at)
		                + (r.estimated_duration_min || ' minutes')::interval
		                + (:bufferMinutes || ' minutes')::interval
		            )
		        )
		""", nativeQuery = true)
	boolean existsBlockingRideForCreator(@Param("userId") Long userId, @Param("statuses") Collection<String> statuses, @Param("newStart") LocalDateTime newStart, @Param("newEnd") LocalDateTime newEnd, @Param("bufferMinutes") int bufferMinutes);

	@Query("""
			SELECT COUNT(r)
			FROM Ride r
			WHERE r.driver.id = :driverId
			  AND r.status = rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus.SCHEDULED
			  AND r.scheduledAt >= :from
			""")
	long countScheduledForDriverFrom(@Param("driverId") Long driverId, @Param("from") LocalDateTime from);

	
    Collection<Ride> findByDriverIdAndStatusOrderByCreatedAtDesc(Long driverId, RideStatus status);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime from);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime to);

    Collection<Ride> findByDriverIdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long driverId, RideStatus status, LocalDateTime from, LocalDateTime to);

    @Query("""
    SELECT DISTINCT r
    FROM Ride r
    LEFT JOIN r.passengers p
    WHERE
        (r.driver.id = :userId OR r.createdBy.id = :userId OR p.id = :userId)
        AND r.status IN :statuses
        AND r.createdAt >= :from
        AND r.createdAt <= :to
""")
    Collection<Ride> findAdminRideHistoryForUser(
            @Param("userId") Long userId,
            @Param("statuses") Collection<RideStatus> statuses,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
    
    Optional<Ride> findFirstByDriverIdAndStatusInOrderByCreatedAtDesc(Long driverId, Collection<RideStatus> statuses);

    @Query("""
    SELECT r FROM Ride r 
    LEFT JOIN r.passengers p 
    WHERE r.status = rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus.ACTIVE 
    AND (r.createdBy.id = :userId OR p.id = :userId)
""")
    Optional<Ride> findActiveRideForPassenger(@Param("userId") Long userId);
}
