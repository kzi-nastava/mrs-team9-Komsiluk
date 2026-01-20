package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;

@Repository
public interface InconsistencyReportRepository extends JpaRepository<InconsistencyReport, Long> {

    List<InconsistencyReport> findByRide_IdOrderByCreatedAtDesc(Long rideId);

    List<InconsistencyReport> findByRide_IdAndReporter_IdOrderByCreatedAtDesc(Long rideId, Long reporterId);

    List<InconsistencyReport> findByReporter_IdOrderByCreatedAtDesc(Long reporterId);
}
