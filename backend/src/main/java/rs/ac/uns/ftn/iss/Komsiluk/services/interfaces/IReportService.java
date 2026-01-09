package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.time.LocalDate;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.report.RideReportDTO;

public interface IReportService {

	public RideReportDTO getUserReport(Long userId, LocalDate startDate, LocalDate endDate);

    public RideReportDTO getAllDriversReport(LocalDate startDate, LocalDate endDate);

    public RideReportDTO getAllPassengersReport(LocalDate startDate, LocalDate endDate);

}
