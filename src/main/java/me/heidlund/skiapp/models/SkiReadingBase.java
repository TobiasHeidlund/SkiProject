package me.heidlund.skiapp.models;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public class SkiReadingBase {
    double lat, lon, alt, speed;
    long time;
    float error;

    public SkiReadingBase(SkiReadingBase base) {
        this.lat = base.lat;
        this.lon = base.lon;
        this.alt = base.alt;
        this.speed = base.speed;
        this.time = base.time;
        this.error = base.error;
    }
}
