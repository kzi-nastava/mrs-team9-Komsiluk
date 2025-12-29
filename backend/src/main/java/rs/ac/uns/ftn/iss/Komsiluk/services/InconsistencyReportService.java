package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.InconsistencyReportRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IInconsistencyReportService;

@Service
public class InconsistencyReportService implements IInconsistencyReportService {

    @Autowired
    private InconsistencyReportRepository reportRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    public InconsistencyReportResponseDTO create(Long rideId, InconsistencyReportCreateDTO dto) {
        if (dto == null || dto.getPassengerId() == null) {
            throw new BadRequestException();
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new BadRequestException();
        }

        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException();
        }

        if (!isPassengerOnRide(ride, dto.getPassengerId())) {
            throw new BadRequestException();
        }

        InconsistencyReport report = new InconsistencyReport();
        report.setRideId(rideId);
        report.setPassengerId(dto.getPassengerId());
        report.setMessage(dto.getMessage().trim());
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);

        return toResponseDTO(report);
    }

    @Override
    public Collection<InconsistencyReportResponseDTO> getByRideId(Long rideId) {
        rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        return reportRepository.findByRideId(rideId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
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
        dto.setRideId(report.getRideId());
        dto.setPassengerId(report.getPassengerId());
        dto.setMessage(report.getMessage());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}
