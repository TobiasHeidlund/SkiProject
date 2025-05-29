package me.heidlund.skiapp;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class generateSkiData {

    @Test
    public void generateSkiData() throws IOException {
            String filePath = "src/test/resources/submitSkiRun-data.csv";
            FileWriter writer = new FileWriter(filePath);

            // Write the CSV header
            writer.write("lat;lon;alt;speed;time;error\n");

            Random random = new Random();
            double lat = 60.0000;  // Starting latitude
            double lon = 15.0000;  // Starting longitude
            double alt = 400;      // Starting altitude
            long time = System.currentTimeMillis();
            float error;

            // Generate lift up data (500 points)
            for (int i = 0; i < 80; i++) {
                lat += random.nextDouble() * 0.0001; // Small latitude change
                lon += random.nextDouble() * 0.0001; // Small longitude change
                alt += 2; // Gradually increase altitude
                error = random.nextFloat() * 2; // Random error between 0-2
                writer.write(formatCsvRow(lat, lon, alt, 0.5 + random.nextDouble() * 1.5, time, error));
                time += (long) (1000 + random.nextFloat()* 500L); // Increment time by 1 second
            }

            // Generate skiing down data (1000 points)
            for (int i = 0; i < 100; i++) {
                lat += random.nextDouble() * 0.0005; // Larger latitude change
                lon += random.nextDouble() * 0.0005; // Larger longitude change
                alt -= 1.1 + random.nextDouble() * 3; // Gradually decrease altitude
                double speed = 5 + random.nextDouble() * 45; // Speed between 5-50 km/h
                error = random.nextFloat() * 5; // Random error between 0-5
                writer.write(formatCsvRow(lat, lon, alt, speed, time, error));
                 time += (long) (1000 + random.nextFloat()* 500L); // Increment time by 1 second
            }

             // Generate lift up data (500 points)
            for (int i = 0; i < 80; i++) {
                lat += random.nextDouble() * 0.0001; // Small latitude change
                lon += random.nextDouble() * 0.0001; // Small longitude change
                alt += 2; // Gradually increase altitude
                error = random.nextFloat() * 2; // Random error between 0-2
                writer.write(formatCsvRow(lat, lon, alt, 0.5 + random.nextDouble() * 1.5, time, error));
                time += (long) (1000 + random.nextFloat()* 500L); // Increment time by 1 second
            }

            for (int i = 0; i < 120; i++) {
                lat += random.nextDouble() * 0.0005; // Larger latitude change
                lon += random.nextDouble() * 0.0005; // Larger longitude change
                alt -= 1.1 + random.nextDouble() * 3; // Gradually decrease altitude
                double speed = 5 + random.nextDouble() * 45; // Speed between 5-50 km/h
                error = random.nextFloat() * 5; // Random error between 0-5
                writer.write(formatCsvRow(lat, lon, alt, speed, time, error));
                 time += (long) (1000 + random.nextFloat()* 500L); // Increment time by 1 second
            }

            writer.close();
        }
        private static String formatCsvRow(double lat, double lon, double alt, double speed, long time, float error) {
            return String.format("%.6f;%.6f;%.2f;%.2f;%d;%.2f\n", lat, lon, alt, speed, time, error);
        }

}
