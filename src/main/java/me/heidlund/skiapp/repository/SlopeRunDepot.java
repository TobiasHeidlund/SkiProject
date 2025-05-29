package me.heidlund.skiapp.repository;

import me.heidlund.skiapp.models.SlopeRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SlopeRunDepot extends JpaRepository<SlopeRun, Long> {

    @Query("SELECT r FROM SlopeRun r WHERE r.start_time BETWEEN :start AND :stop order by r.start_time")
    SlopeRun getSlopeRunWithinSpanBefore(long start, long stop);
    @Query("SELECT r FROM SlopeRun r WHERE r.end_time BETWEEN :start AND :stop order by r.end_time")
    SlopeRun getSlopeRunWithinSpanAfter(long start, long stop);
    @Query("SELECT r FROM SlopeRun r WHERE r.start_time BETWEEN :start AND :stop order by r.start_time")
    List<SlopeRun> getAllByTime(long start, long stop);

}
