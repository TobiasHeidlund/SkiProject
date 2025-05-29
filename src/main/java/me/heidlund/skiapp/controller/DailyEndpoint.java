package me.heidlund.skiapp.controller;

import me.heidlund.skiapp.models.DailySummery;
import me.heidlund.skiapp.services.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/daily")
public class DailyEndpoint {
    @Autowired
    private DailyService dailyService;

    @GetMapping("/active-days")
    public ResponseEntity<List<LocalDate>> getDays() {
        List<LocalDate> s = dailyService.getActiveDays();
        if(s == null || s.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(s);
    }
    @GetMapping("/summery")
    public ResponseEntity<DailySummery> getSummery(@RequestParam LocalDate date) {
        if (date == null) return ResponseEntity.badRequest().build();
        DailySummery summery = dailyService.getSummery(date);
        if(summery == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(summery);
    }

}
