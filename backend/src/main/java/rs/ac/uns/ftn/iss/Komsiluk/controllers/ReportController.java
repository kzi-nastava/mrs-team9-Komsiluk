package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.report.RideReportDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IReportService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IReportService reportService;
    private final IUserService userService;

    @Autowired
    public ReportController(IReportService reportService, IUserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('DRIVER', 'PASSENGER')")
    @GetMapping("/users/{id}")
    public ResponseEntity<RideReportDTO> getUserReport(@PathVariable Long id, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,@RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        RideReportDTO dto = reportService.getUserReport(id, start, end);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    public ResponseEntity<RideReportDTO> getAllDriversReport(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start, @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        RideReportDTO dto = reportService.getAllDriversReport(start, end);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/passengers")
    public ResponseEntity<RideReportDTO> getAllPassengersReport( @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start, @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        RideReportDTO dto = reportService.getAllPassengersReport(start, end);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/by-email")
    public ResponseEntity<RideReportDTO> getUserReportByEmail(@RequestParam("email") String email, @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start, @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new NotFoundException();
        }
        RideReportDTO dto = reportService.getUserReport(user.getId(), start, end);
        return ResponseEntity.ok(dto);
    }
}