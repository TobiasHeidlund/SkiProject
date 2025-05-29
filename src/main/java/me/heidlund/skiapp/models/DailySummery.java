package me.heidlund.skiapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DailySummery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    @Column(nullable = false)
    LocalDate date;
    double total_altitude, total_distance, avg_speed, top_speed;

    public void addData(SlopeRun slopeRun) {
        total_altitude += slopeRun.getTotal_altitude();
        total_distance += slopeRun.getTotal_distance();
        avg_speed += slopeRun.getAvg_speed();
        avg_speed /= 2;
        top_speed = Math.max(top_speed, slopeRun.getTop_speed());

    }
}
