package me.heidlund.skiapp.repository;

import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface ReadingDepot extends JpaRepository<SkiReadingDb, Long> {


    @Query("SELECT r FROM SkiReadingDb r WHERE r.id < :index ORDER BY r.id DESC LIMIT :Limit")
    List<SkiReadingDb> findBefore(long index, int limit);

    @Query("SELECT r FROM SkiReadingDb r WHERE r.id BETWEEN :start AND :stop ORDER BY r.time ASC")
    List<SkiReadingDb> getRange(long start, long stop);

    @Query("SELECT r FROM SkiReadingDb r WHERE r.run.id = :id ORDER BY r.time ASC")
    List<SkiReadingDb> getAllByRunId(long id);

    @Query("SELECT r FROM SkiReadingDb r WHERE r.run.id IS NULL AND r.status = 'undefined' ORDER BY r.time ASC")
    List<SkiReadingDb> getWithoutDirection();
}
