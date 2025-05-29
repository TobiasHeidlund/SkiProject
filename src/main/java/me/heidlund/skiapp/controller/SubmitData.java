package me.heidlund.skiapp.controller;

import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import me.heidlund.skiapp.services.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/submit")
public class SubmitData {

    @Autowired
    SubmitService submitService;

    @PostMapping
    public ResponseEntity<String> SubmitData(@RequestBody List<SkiReadingBase> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("Data is null or empty");
            return ResponseEntity.badRequest().build(); }
        submitService.saveData(data);
        return ResponseEntity.ok().body("OK");
    }
}
