package me.heidlund.skiapp.controller;


import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import me.heidlund.skiapp.models.SlopeRun;
import me.heidlund.skiapp.services.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/run")
public class RunEndpoint {
    @Autowired
    RunService runService;

    @GetMapping("")
    public ResponseEntity<List<SlopeRun>> getRuns(@RequestParam LocalDate date) {
        if (date == null) return ResponseEntity.badRequest().build();
        List<SlopeRun> runs = runService.getRuns(date);
        if (runs == null || runs.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(runs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<SkiReadingDb>> getRun(@PathVariable long id) {
        if (id < 0) return ResponseEntity.badRequest().build();
        List<SkiReadingDb> run = runService.getRunData(id);
        if (run == null || run.isEmpty())  return ResponseEntity.notFound().build();
        return ResponseEntity.ok(run);
    }

}
