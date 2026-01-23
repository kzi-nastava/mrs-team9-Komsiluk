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

