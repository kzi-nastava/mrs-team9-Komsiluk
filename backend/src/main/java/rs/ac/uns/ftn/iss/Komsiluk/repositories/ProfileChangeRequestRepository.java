package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.ProfileChangeRequest;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.ChangeRequestStatus;

@Repository
public class ProfileChangeRequestRepository {
	private final Map<Long, ProfileChangeRequest> storage = new HashMap<>();
    private long nextId = 1L;

    public ProfileChangeRequest save(ProfileChangeRequest req) {
        if (req.getId() == null) {
            req.setId(nextId++);
        }
        storage.put(req.getId(), req);
        return req;
    }

    public ProfileChangeRequest findById(Long id) {
        return storage.get(id);
    }

    public Collection<ProfileChangeRequest> findAll() {
        return storage.values();
    }

    public Collection<ProfileChangeRequest> findByStatus(ChangeRequestStatus status) {
        return storage.values().stream().filter(r -> r.getStatus() == status).toList();
    }
}
