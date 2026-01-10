package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.ProfileChangeRequest;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.ChangeRequestStatus;

@Repository
public interface ProfileChangeRequestRepository extends JpaRepository<ProfileChangeRequest, Long>{

    public Collection<ProfileChangeRequest> findByStatus(ChangeRequestStatus status);
}
