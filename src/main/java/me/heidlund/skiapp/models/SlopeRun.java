package me.heidlund.skiapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SlopeRun {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    long  start_time, end_time;
    double total_altitude, avg_speed, top_speed,total_distance;

    public SlopeRun(long start_time, long end_time, double total_altitude, double avg_speed, double top_speed, double total_distance) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.total_altitude = total_altitude;
        this.avg_speed = avg_speed;
        this.top_speed = top_speed;
        this.total_distance = total_distance;
    }

    public void merge(long startTime, long stopTime, double totAlt, double avgSpeed, double topSpeed, double totDist) {
        this.start_time = Math.min(this.start_time, startTime);
        this.end_time = Math.max(this.end_time, stopTime);
        this.total_altitude = this.total_altitude + totAlt;
        //TODO: FIX THE AVERAGE CACULATIONS TO NOT BE AS WEIGHTED
        this.avg_speed = (this.avg_speed + avgSpeed)/2;
        this.top_speed = Math.max(this.top_speed, topSpeed);
        this.total_distance = this.total_distance + totDist;

    }
}
