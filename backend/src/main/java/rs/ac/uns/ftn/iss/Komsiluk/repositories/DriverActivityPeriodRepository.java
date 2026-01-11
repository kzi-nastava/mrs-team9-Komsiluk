package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverActivityPeriod;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

@Repository
public interface DriverActivityPeriodRepository extends JpaRepository<DriverActivityPeriod, Long> {

    public List<DriverActivityPeriod> findByDriver(User driver);
    
    public List<DriverActivityPeriod> findByDriverAndEndTimeAfter(User driver, LocalDateTime from);

    public DriverActivityPeriod findTopByDriverAndEndTimeIsNullOrderByStartTimeDesc(User driver);
}
