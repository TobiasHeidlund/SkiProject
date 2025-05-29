package me.heidlund.skiapp.services;

import me.heidlund.skiapp.models.DailySummery;
import me.heidlund.skiapp.models.SlopeRun;
import me.heidlund.skiapp.repository.DailySummaryDepot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
public class DailyService {
    @Autowired
    DailySummaryDepot dailySummaryDepot;

    public DailySummery getOrCreateSummery(SlopeRun slopeRun) {
        LocalDate currentDate = Instant.ofEpochMilli(slopeRun.getStart_time())
                                        .atZone(ZoneId.systemDefault()) // Use the system's default time zone
                                        .toLocalDate();

        DailySummery summery = findExisting(slopeRun, currentDate);
        if(summery == null) summery = createSummery(slopeRun, currentDate);
        return summery;
    }

    private DailySummery createSummery(SlopeRun slopeRun, LocalDate currentDate) {

         return dailySummaryDepot.save(new DailySummery(null,currentDate,slopeRun.getTotal_altitude(),
                 slopeRun.getTotal_distance(),slopeRun.getAvg_speed(),slopeRun.getTop_speed()));

    }

    private DailySummery findExisting(SlopeRun slopeRun, LocalDate currentDate) {
        DailySummery summery =  dailySummaryDepot.findByDateEquals(currentDate);
        if (summery != null) summery.addData(slopeRun);
        return summery;
    }

    public List<LocalDate> getActiveDays() {
        List<LocalDate> dates = dailySummaryDepot.findAllDates();
        System.out.println("Found " + dates.size() + " active days");
        dates.sort(LocalDate::compareTo);
        return dates;
    }

    public DailySummery getSummery(LocalDate date) {
        return dailySummaryDepot.findByDateEquals(date);

    }
}
