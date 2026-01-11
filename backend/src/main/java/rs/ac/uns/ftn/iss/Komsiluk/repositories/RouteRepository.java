package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query("""
            SELECT r
            FROM Route r
            WHERE r.startAddress = :start
              AND r.endAddress = :end
              AND ((:stops IS NULL AND r.stops IS NULL) OR r.stops = :stops)
            ORDER BY COALESCE(r.estimatedDurationMin, 2147483647) ASC
            """)
        Optional<Route> findBestByKey(@Param("start") String start, @Param("end") String end, @Param("stops") String stops);
}