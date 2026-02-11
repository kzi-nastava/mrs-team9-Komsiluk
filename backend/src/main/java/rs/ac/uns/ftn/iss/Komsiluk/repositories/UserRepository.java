package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverBasicDTO;

import java.util.Collection;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmailIgnoreCase(String email);

    public User findByEmailIgnoreCase(String email);

    User findByEmail(String email);

    @Query("""
        SELECT u.id
        FROM User u
        WHERE u.role = :role
          AND u.driverStatus = :driverStatus
          AND u.active = true
          AND u.blocked = false
    """)
    List<Long> findDriverIdsByStatus(
            @Param("role") UserRole role,
            @Param("driverStatus") DriverStatus driverStatus
    );

    @Query(value = """
    	    SELECT u.*
    	    FROM users u
    	    JOIN vehicles v ON v.id = u.vehicle_id
    	    WHERE u.role = 'DRIVER'
    	      AND u.driver_status = 'ACTIVE'
    	      AND u.active = true
    	      AND u.blocked = false
    	      AND v.type = :vehicleType
    	      AND v.seat_count >= :minSeatCount
    	      AND (:babyFriendly = FALSE OR v.baby_friendly = TRUE)
    	      AND (:petFriendly  = FALSE OR v.pet_friendly  = TRUE)

    	      AND NOT EXISTS (
    	          SELECT 1
    	          FROM rides r
    	          WHERE r.driver_id = u.id
    	            AND r.status IN ('ACTIVE', 'ASSIGNED', 'SCHEDULED')
    	            AND (
    	                COALESCE(r.scheduled_at, r.start_time, r.created_at) < :newEnd
    	                AND :newStart < COALESCE(r.end_time, :newStart)
    	            )
    	      )
    	""", nativeQuery = true)
    List<User> findAvailableDriversNoConflict(@Param("vehicleType") String vehicleType, @Param("minSeatCount") int minSeatCount, @Param("babyFriendly") boolean babyFriendly, @Param("petFriendly") boolean petFriendly, @Param("newStart") LocalDateTime newStart, @Param("newEnd") LocalDateTime newEnd);

    @Query(value = """
    	    SELECT DISTINCT u.*
    	    FROM users u
    	    JOIN vehicles v ON v.id = u.vehicle_id
    	    JOIN rides r ON r.driver_id = u.id
    	    WHERE u.role = 'DRIVER'
    	      AND u.active = true
    	      AND u.blocked = false
    	      AND u.driver_status IN ('ACTIVE', 'IN_RIDE')
    	      AND v.type = :vehicleType
    	      AND v.seat_count >= :minSeatCount
    	      AND (:babyFriendly = FALSE OR v.baby_friendly = TRUE)
    	      AND (:petFriendly  = FALSE OR v.pet_friendly  = TRUE)

    	      AND r.status = 'ACTIVE'
    	      AND r.end_time IS NOT NULL
    	      AND r.end_time <= :finishBefore
    	      AND r.end_time >= :now
    	""", nativeQuery = true)
		List<User> findDriversFinishingSoon(@Param("vehicleType") String vehicleType,@Param("minSeatCount") int minSeatCount,@Param("babyFriendly") boolean babyFriendly,@Param("petFriendly") boolean petFriendly,@Param("now") LocalDateTime now,@Param("finishBefore") LocalDateTime finishBefore);

	
    @Query("""
   SELECT new rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverBasicDTO(u.id, u.firstName, u.lastName)
   FROM User u
   WHERE u.role = rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole.DRIVER
     AND u.active = true
     AND u.blocked = false
""")
    List<DriverBasicDTO> findDriverBasics();

    List<User> findByIdIn(Collection<Long> ids);
}

