package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestResponseDTO;

public interface IProfileChangeRequestService {
	
    public ProfileChangeRequestResponseDTO createRequest(Long driverId, ProfileChangeRequestCreateDTO dto);
    
    public Collection<ProfileChangeRequestResponseDTO> getPendingRequests();
    
    public ProfileChangeRequestResponseDTO approve(Long requestId, Long adminId);
    
    public ProfileChangeRequestResponseDTO reject(Long requestId, Long adminId, String reason);
}