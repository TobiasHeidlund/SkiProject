package me.heidlund.skiapp.repository;

import me.heidlund.skiapp.models.DailySummery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DailySummaryDepot extends JpaRepository<DailySummery,Long> {

    @Query("SELECT r FROM DailySummery r WHERE r.date = :currentDate ")
    DailySummery findByDateEquals(LocalDate currentDate);

    @Query("SELECT r.date FROM DailySummery r where r.date BETWEEN :first and :last")
    List<LocalDate> findWithinMonth(LocalDate first, LocalDate last);

    @Query("SELECT r.date FROM DailySummery r order by r.date DESC")
    List<LocalDate> findAllDates();
}
