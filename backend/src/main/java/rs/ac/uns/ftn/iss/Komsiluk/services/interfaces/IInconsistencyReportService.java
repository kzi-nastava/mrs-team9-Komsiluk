package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;

public interface IInconsistencyReportService {

    InconsistencyReportResponseDTO create(Long rideId, InconsistencyReportCreateDTO dto);

    Collection<InconsistencyReportResponseDTO> getByRideId(Long rideId);
}
