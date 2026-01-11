package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.FavoriteRoute;

@Repository
public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {

    public Collection<FavoriteRoute> findAllByUserId(Long userId);

}