package me.heidlund.skiapp;

import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import me.heidlund.skiapp.models.Status;
import me.heidlund.skiapp.services.SubmitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SubmitServiceTests {

    @Autowired
    private SubmitService service;
    private List<SkiReadingBase> skiReadings;
    @BeforeEach
    void setUp() throws IOException {
        skiReadings = loadCsvData("src/test/resources/submitSkiRun-data.csv");
    }

    private List<SkiReadingBase> loadCsvData(String filePath) throws IOException {
        List<SkiReadingBase> skiReadings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                double lat = Double.parseDouble(fields[0].replace(",", "."));
                double lon =  Double.parseDouble(fields[1].replace(",", "."));
                double alt =  Double.parseDouble(fields[2].replace(",", "."));
                float speed =  (float)Double.parseDouble(fields[3].replace(",", "."));
                long time = Long.parseLong(fields[4]);
                float error = Float.parseFloat(fields[5].replace(",", "."));
                System.out.println("lat: " + lat + " lon: " + lon + " alt: " + alt + " speed: " + speed + " time: " + time + " error: " + error);
                skiReadings.add(new SkiReadingBase(lat, lon, alt, speed, time, error));
            }
        }
        return skiReadings;
    }


    @Test
    public void testSavingAlgorithm(){
        service.saveData(skiReadings);


    }

}
