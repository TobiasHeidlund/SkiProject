package me.heidlund.skiapp.services;

import me.heidlund.skiapp.models.DailySummery;
import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import me.heidlund.skiapp.models.SlopeRun;
import me.heidlund.skiapp.repository.ReadingDepot;
import me.heidlund.skiapp.repository.SlopeRunDepot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RunService {
     @Autowired
    private SlopeRunDepot slopeRunDepot;
    @Autowired
    DailyService dailyService;
    @Autowired
    ReadingDepot readingDepot;


     private SlopeRun getExistingRun(long startTime,long endTime){
         SlopeRun before =  slopeRunDepot.getSlopeRunWithinSpanBefore(startTime-60*1000,endTime);
         if(before == null) before = slopeRunDepot.getSlopeRunWithinSpanAfter(startTime,endTime+60*1000);
         if(before != null) dailyService.getOrCreateSummery(before);

         return before;
     }


    private SlopeRun addSlopeRun(long startTime, long stopTime, double totAlt, double avgSpeed, double topSpeed, double totDist){
        SlopeRun slopeRun = new SlopeRun(null,startTime,stopTime,totAlt,avgSpeed,topSpeed,totDist);
        dailyService.getOrCreateSummery(slopeRun);


        return slopeRunDepot.save(slopeRun);
    }


    public SlopeRun getOrCreateSlopeRun(long startTime, long stopTime, double totAlt, double avgSpeed, double topSpeed, double totDist) {
         SlopeRun slopeRun = getExistingRun(startTime,stopTime);
         if(slopeRun == null){

             return addSlopeRun(startTime,stopTime,totAlt,avgSpeed,topSpeed,totDist);
         }
         slopeRun.merge(startTime,stopTime,totAlt,avgSpeed,topSpeed,totDist);
         return slopeRun;

    }

    public List<SlopeRun> getRuns(LocalDate date) {
        long startTime = date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        long stopTime = date.plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        return slopeRunDepot.getAllByTime(startTime, stopTime);
    }

    public List<SkiReadingDb> getRunData(long id) {
        return readingDepot.getAllByRunId(id);
    }



}
