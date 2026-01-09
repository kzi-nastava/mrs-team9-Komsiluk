package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.report.DailyValueDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.report.RideReportDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IReportService;

@Service
public class ReportService implements IReportService {

	@Autowired
    private RideRepository rideRepository;
	@Autowired
    private UserRepository userRepository;

    @Override
    public RideReportDTO getUserReport(Long userId, LocalDate start, LocalDate end) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException();
        }

        Collection<Ride> allRides = rideRepository.findAll();

        Stream<Ride> ridesStream = allRides.stream()
                // .filter(r -> r.getStatus() == RideStatus.FINISHED)
                .filter(r -> belongsToUser(r, user))
                .filter(r -> isInRange(getRideDate(r), start, end));

        return buildReport(ridesStream, start, end);
    }

    @Override
    public RideReportDTO getAllDriversReport(LocalDate start, LocalDate end) {
        Collection<Ride> allRides = rideRepository.findAll();

        Stream<Ride> ridesStream = allRides.stream()
                // .filter(r -> r.getStatus() == RideStatus.FINISHED)
                .filter(r -> r.getDriver() != null)
                .filter(r -> isInRange(getRideDate(r), start, end));

        return buildReport(ridesStream, start, end);
    }

    @Override
    public RideReportDTO getAllPassengersReport(LocalDate start, LocalDate end) {
        Collection<Ride> allRides = rideRepository.findAll();

        Stream<Ride> ridesStream = allRides.stream()
                // .filter(r -> r.getStatus() == RideStatus.FINISHED)
                .filter(r -> r.getCreatedBy() != null)
                .filter(r -> isInRange(getRideDate(r), start, end));

        return buildReport(ridesStream, start, end);
    }

    private boolean belongsToUser(Ride ride, User user) {
        if (user.getRole() == UserRole.DRIVER) {
            return ride.getDriver() != null && ride.getDriver().getId().equals(user.getId());
        } else {
            return ride.getCreatedBy() != null && ride.getCreatedBy().getId().equals(user.getId());
        }
    }

    private LocalDate getRideDate(Ride ride) {
        if (ride.getEndTime()!= null) {
            return ride.getEndTime().toLocalDate();
        } else if (ride.getStartTime() != null) {
            return ride.getStartTime().toLocalDate();
        } else {
            return ride.getCreatedAt().toLocalDate();
        }
    }

    private boolean isInRange(LocalDate d, LocalDate start, LocalDate end) {
        if (d == null) return false;
        return !d.isBefore(start) && !d.isAfter(end);
    }

    private RideReportDTO buildReport(Stream<Ride> ridesStream, LocalDate start, LocalDate end) {
        Map<LocalDate, DailyAggregate> perDay = new TreeMap<>();

        ridesStream.forEach(ride -> {
            LocalDate day = getRideDate(ride);
            DailyAggregate agg = perDay.computeIfAbsent(day, d -> new DailyAggregate());
            agg.ridesCount++;
            agg.distanceKm += ride.getDistanceKm();
            if (ride.getPrice() != null) {
                agg.money = agg.money.add(ride.getPrice());
            }
        });

        long totalRides = perDay.values().stream().mapToLong(a -> a.ridesCount).sum();

        double totalDistance = perDay.values().stream().mapToDouble(a -> a.distanceKm).sum();

        BigDecimal totalMoney = perDay.values().stream().map(a -> a.money).reduce(BigDecimal.ZERO, BigDecimal::add);

        long days = ChronoUnit.DAYS.between(start, end) + 1;
        double avgRides = days > 0 ? (double) totalRides / days : 0.0;
        double avgDistance = days > 0 ? totalDistance / days : 0.0;
        BigDecimal avgMoney = days > 0 ? totalMoney.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<DailyValueDTO> ridesPerDay = perDay.entrySet().stream().map(e -> new DailyValueDTO(e.getKey(), e.getValue().ridesCount)).collect(Collectors.toList());

        List<DailyValueDTO> distancePerDay = perDay.entrySet().stream().map(e -> new DailyValueDTO(e.getKey(), e.getValue().distanceKm)).collect(Collectors.toList());

        List<DailyValueDTO> moneyPerDay = perDay.entrySet().stream().map(e -> new DailyValueDTO(e.getKey(), e.getValue().money.doubleValue())).collect(Collectors.toList());

        RideReportDTO dto = new RideReportDTO();
        dto.setRidesPerDay(ridesPerDay);
        dto.setTotalRides(totalRides);
        dto.setAverageRidesPerDay(avgRides);

        dto.setDistancePerDay(distancePerDay);
        dto.setTotalDistanceKm(totalDistance);
        dto.setAverageDistanceKmPerDay(avgDistance);

        dto.setMoneyPerDay(moneyPerDay);
        dto.setTotalMoney(totalMoney);
        dto.setAverageMoneyPerDay(avgMoney);

        return dto;
    }

    private static class DailyAggregate {
        long ridesCount = 0;
        double distanceKm = 0.0;
        BigDecimal money = BigDecimal.ZERO;
    }
}