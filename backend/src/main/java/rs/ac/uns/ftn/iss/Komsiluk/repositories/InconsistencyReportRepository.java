package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;

@Repository
public interface InconsistencyReportRepository extends JpaRepository<InconsistencyReport, Long> {

    List<InconsistencyReport> findByRide_IdOrderByCreatedAtDesc(Long rideId);

    List<InconsistencyReport> findByRide_IdAndReporter_IdOrderByCreatedAtDesc(Long rideId, Long reporterId);

    List<InconsistencyReport> findByReporter_IdOrderByCreatedAtDesc(Long reporterId);

    @Query("SELECT r FROM InconsistencyReport r JOIN FETCH r.reporter WHERE r.ride.id = :rideId ORDER BY r.createdAt DESC")
    Collection<InconsistencyReport> findByRideIdWithReporter(@Param("rideId") Long rideId);
}
