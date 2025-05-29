package me.heidlund.skiapp.models;

import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ski_reading_db")
public class SkiReadingDb  extends SkiReadingBase{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    @ManyToOne()
    @JoinColumn(name= "SlopeId",nullable = true)
    SlopeRun run = null;

    @Enumerated(EnumType.STRING)
    Status status = Status.undefined;


    public SkiReadingDb(double lat, double lon, double alt, double speed, long time, float error, Long id, SlopeRun run, Status status) {
        super(lat, lon, alt, speed, time, error);
        this.id = id;
        this.run = run;
        this.status = status;
    }
    public SkiReadingDb(double lat, double lon, double alt, double speed, long time, float error) {
        super(lat, lon, alt, speed, time, error);
        this.id = null;
        this.run = null;
        this.status = Status.undefined;
    }

    public SkiReadingDb(Long id, SlopeRun run, Status status, SkiReadingBase base) {
        super(base);
        this.id = id;
        this.run = run;
        this.status = status;
    }

    public SkiReadingDb(SkiReadingBase base) {
        super(base);
        this.id = null;
        this.run = null;
        this.status = Status.undefined;
    }
}
