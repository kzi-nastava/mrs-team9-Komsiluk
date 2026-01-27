package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.ProfileChangeRequest;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.ChangeRequestStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.ProfileChangeRequestDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.ProfileChangeRequestRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IProfileChangeRequestService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;

@Service
public class ProfileChangeRequestService implements IProfileChangeRequestService {

	@Autowired
    private ProfileChangeRequestRepository repo;
	@Autowired
    private IUserService userService;
	@Autowired
	private IVehicleService vehicleService;
	@Autowired
	private ProfileChangeRequestDTOMapper mapper;

    @Override
    public ProfileChangeRequestResponseDTO createRequest(Long driverId, ProfileChangeRequestCreateDTO dto) {
        User driver = userService.findById(driverId);

        ProfileChangeRequest req = new ProfileChangeRequest();
        req.setRequestedAt(LocalDateTime.now());
        req.setStatus(ChangeRequestStatus.PENDING);
        req.setDriver(driver);

        mapper.fromCreateDTO(dto, req);
        repo.save(req);

        return mapper.toResponseDTO(req);
    }

    @Override
    public Collection<ProfileChangeRequestResponseDTO> getPendingRequests() {
        return repo.findByStatus(ChangeRequestStatus.PENDING).stream().map(req -> mapper.toResponseDTO(req)).toList();
    }

    @Override
    public ProfileChangeRequestResponseDTO approve(Long requestId, Long adminId) {
        ProfileChangeRequest req = repo.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));

        User driver = req.getDriver();
        Vehicle vehicle = driver.getVehicle();
        User admin = userService.findById(adminId);
        req.setAdmin(admin);
        req.setStatus(ChangeRequestStatus.APPROVED);

        mapper.fromRequestToUser(req, driver);
        mapper.fromRequestToVehicle(req, vehicle);

        userService.save(driver);
        vehicleService.save(vehicle);
        repo.save(req);

        return mapper.toResponseDTO(req);
    }

    @Override
    public ProfileChangeRequestResponseDTO reject(Long requestId, Long adminId) {
        ProfileChangeRequest req = repo.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));

        User admin = userService.findById(adminId);
        req.setAdmin(admin);
        req.setStatus(ChangeRequestStatus.REJECTED);

        repo.save(req);
        
        return mapper.toResponseDTO(req);
    }
}
