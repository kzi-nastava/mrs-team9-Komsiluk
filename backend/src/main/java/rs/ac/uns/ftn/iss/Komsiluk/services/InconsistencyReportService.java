package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.nio.file.AccessDeniedException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.InconsistencyReportRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IInconsistencyReportService;

@Service
public class InconsistencyReportService implements IInconsistencyReportService {

    @Autowired
    private InconsistencyReportRepository reportRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public InconsistencyReportResponseDTO create(Long rideId, InconsistencyReportCreateDTO dto) {
        if (dto == null || dto.getMessage() == null || dto.getMessage().trim().isEmpty())
            throw new BadRequestException();

        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);
        if (ride.getStatus() != RideStatus.ACTIVE) throw new BadRequestException();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User reporter = userRepository.findByEmail(email);
        Long reporterId = reporter.getId();

        boolean isDriver = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DRIVER"));
        boolean isPassenger = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PASSENGER"));

        if (!isDriver && !isPassenger) throw new BadRequestException();

        // autorizacija nad vožnjom
        if (isDriver) {
            if (ride.getDriver() == null || !ride.getDriver().getId().equals(reporterId)) {
                throw new org.springframework.security.access.AccessDeniedException("Not your ride.");
            }
        } else { // passenger
            boolean isCreator = ride.getCreatedBy() != null && ride.getCreatedBy().getId().equals(reporterId);
            boolean isLinkedPassenger = ride.getPassengers() != null &&
                    ride.getPassengers().stream().anyMatch(u -> u != null && u.getId().equals(reporterId));

            if (!isCreator && !isLinkedPassenger) {
                throw new org.springframework.security.access.AccessDeniedException("Not on this ride.");
            }
        }


        InconsistencyReport report = new InconsistencyReport();
        report.setRide(ride);
        report.setReporter(reporter);
        report.setReporterRole(isDriver ? UserRole.DRIVER : UserRole.PASSENGER);
        report.setMessage(dto.getMessage().trim());

        reportRepository.save(report);

        return toResponseDTO(report);
    }




    private boolean isValidReporter(Ride ride, User reporter) {
        if (ride.getDriver().equals(reporter)) {
            return true;
        }

        if (ride.getCreatedBy().equals(reporter)) {
            return true;
        }

        if (ride.getPassengers().contains(reporter)) {
            return true;
        }

        return false;
    }


    @Override
    public Collection<InconsistencyReportResponseDTO> getByRideId(Long rideId) {
        rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        return reportRepository.findByRide_IdOrderByCreatedAtDesc(rideId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private boolean isPassengerOnRide(Ride ride, Long passengerId) {
        if (ride.getPassengers() == null) return false;
        for (User u : ride.getPassengers()) {
            if (u != null && u.getId() != null && u.getId().equals(passengerId)) {
                return true;
            }
        }
        return false;
    }

    private InconsistencyReportResponseDTO toResponseDTO(InconsistencyReport report) {
        InconsistencyReportResponseDTO dto = new InconsistencyReportResponseDTO();
        dto.setId(report.getId());
        dto.setRideId(report.getRide().getId());
        dto.setReporterId(report.getReporter().getId());
        dto.setMessage(report.getMessage());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setReporterRole(report.getReporterRole());
        return dto;
    }
}
