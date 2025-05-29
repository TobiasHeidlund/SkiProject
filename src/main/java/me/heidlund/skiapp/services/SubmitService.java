package me.heidlund.skiapp.services;


import me.heidlund.skiapp.models.SkiReadingBase;
import me.heidlund.skiapp.models.SkiReadingDb;
import me.heidlund.skiapp.models.SlopeRun;
import me.heidlund.skiapp.models.Status;
import me.heidlund.skiapp.repository.ReadingDepot;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
public class SubmitService {
    @Autowired
    private ReadingDepot readingDepot;
    @Autowired
    private RunService runService;

    public boolean saveData(List<SkiReadingBase> summery){
        long dataPoints = readingDepot.count();
        List<SkiReadingDb> dbData = new ArrayList<SkiReadingDb>();
        for (SkiReadingBase skiReadingBase : summery) {
            dbData.add(new SkiReadingDb(skiReadingBase));
        }

        List<SkiReadingDb> saved = readingDepot.saveAll(dbData);
        //calculateSummaries(saved.get(0).getId(),saved.get(saved.size()-1).getId());
        return summery.size() == saved.size();

    }





    @Async
    public void calculateSummaries(long startIndex, long stopIndex){
        //TODO: VERIFY TIME
        //TODO: CAN BE MORE EFFICIENT BY REDUCING DB CALLS
        List<SkiReadingDb> during = readingDepot.getRange(startIndex,stopIndex);
        List<SkiReadingDb> allUnknownPoints = new ArrayList<>();
        allUnknownPoints.addAll(during);

        System.out.println("Calculating summaries for " + during.size() + " points between " + startIndex + " and " + stopIndex);

        int sliceSize = 20;
        ArrayList<SimpleRegression> regressions = new ArrayList<>();
        for (int i = 0; i < (allUnknownPoints.size()-(sliceSize-1)); i+=sliceSize) {
            regressions.add(regress(allUnknownPoints.subList(i,i+sliceSize)));
        }


        Iterator<SimpleRegression> regit = regressions.iterator();
        Status lastDirection = getDirection(regit.next());
        int prevQuali = 0;
        int currentQuali = 0;
        while (regit.hasNext()) {
         SimpleRegression reg = regit.next();
         currentQuali+=sliceSize;

         Status direction = getDirection(reg);
         if(direction != lastDirection){
             System.out.println("Direction change: " + lastDirection + " -> " + direction + " at " + currentQuali);
             qualifyData(lastDirection,allUnknownPoints.subList(prevQuali,currentQuali));
             prevQuali = currentQuali;
             lastDirection = direction;
         }

        }

        // if there are more points after the last regression
        if ( prevQuali != currentQuali){
            List<SkiReadingDb> next = readingDepot.getRange(stopIndex+1, stopIndex+1);
            if (next != null && !next.isEmpty()) {
                if( next.getFirst().getStatus() != lastDirection) {
                    System.out.println("Direction change: " + lastDirection + " -> " + next.getFirst().getStatus() + " at " + currentQuali);
                    qualifyData(next.getFirst().getStatus(), allUnknownPoints.subList(prevQuali, currentQuali));
                }
            }
        }
    }

    private void qualifyData(Status direction, List<SkiReadingDb> skiReadings) {
          SlopeRun run = null;
        if(direction == Status.skiing){
            run = runService.getOrCreateSlopeRun(skiReadings.get(0).getTime(),
                    skiReadings.get(skiReadings.size()-1).getTime(),
                   skiReadings.get(skiReadings.size()-1).getAlt()-skiReadings.get(0).getAlt(),

                    calcAvgSpeed(skiReadings),

                    calcAverageSpeed(skiReadings),
                    getTopSpeed(skiReadings));
        }


        for (SkiReadingDb skiReading : skiReadings) {
            skiReading.setStatus(direction);
            if(run != null) skiReading.setRun(run);
        }
        readingDepot.saveAll(skiReadings);
    }

    private double calcAvgSpeed(List<SkiReadingDb> skiReadings) {
        double lon = Math.pow(skiReadings.get(skiReadings.size()-1).getLon()-skiReadings.get(0).getLon(),2);
        double lat = Math.pow(skiReadings.get(skiReadings.size()-1).getLat()-skiReadings.get(0).getLat(),2);
        if (lon == 0 || lat == 0) return 0;
        return Math.sqrt(lat/lon);
    }

    private double getTopSpeed(List<SkiReadingDb> skiReadings) {
        double speed = Double.MIN_VALUE;
        for (SkiReadingDb reading : skiReadings) {
            if (reading.getSpeed() > speed) {
                speed = reading.getSpeed();
            }
        }

        return speed;
    }

    private double calcAverageSpeed(List<SkiReadingDb> skiReadings) {
    if (skiReadings == null || skiReadings.isEmpty()) {
        return 0; // Handle edge case where the list is null or empty
    }

    double totalSpeed = 0;

    for (SkiReadingDb reading : skiReadings) {
        totalSpeed += reading.getSpeed();
    }

    return totalSpeed / skiReadings.size();
    }

    private Status getDirection(SimpleRegression next) {
        if(next.getSlope() > .2){
            //UP
            return Status.lifting;
        }else if(next.getSlope() < -.5){
            //DOWN
            return Status.skiing;
        }else{ return Status.notMoving;
        }
    }

    private SimpleRegression regress(List<SkiReadingDb> skiReadings) {
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < skiReadings.size(); i++) {
            regression.addData(i,skiReadings.get(i).getAlt());
        }
        return regression;
    }

    @Scheduled(fixedRate = 1000*60)
    public void calculateSummaries(){
        System.out.println("Calculating summaries");
        List<SkiReadingDb> undefinedPoints = readingDepot.getWithoutDirection();

        if (undefinedPoints.isEmpty()) {
            System.out.println("No undefined points found, skipping summary calculation.");
            return;
        }
        //APPARENTLY NEEDED TO SORT due to getting some index errors when retriving
        undefinedPoints.sort(Comparator.comparingLong(SkiReadingDb::getId));
        //check for multirun
        List<List<SkiReadingDb>> runs = splitRuns(undefinedPoints);
        for (List<SkiReadingDb> run : runs) {
            if(run.size() >= 20){
                long startIndex = run.getFirst().getId();
                long stopIndex = run.getLast().getId();
                calculateSummaries(startIndex,stopIndex);
            }else {
                System.out.println("Not enough data to calculate summaries");
            }
        }



    }

    private List<List<SkiReadingDb>> splitRuns(List<SkiReadingDb> undefinedPoints) {
        List<List<SkiReadingDb>> runs = new ArrayList<>();

        int first = 0;
        long lastid = undefinedPoints.getFirst().getId()-1;
        for (int i = 0; i < undefinedPoints.size(); i++) {
            if (undefinedPoints.get(i).getId() != lastid + 1) {
                runs.add(undefinedPoints.subList(first, i - 1));
                first = i;
            }
            lastid = undefinedPoints.get(i).getId();
        }
        if (first != undefinedPoints.size()) {
            runs.add(undefinedPoints.subList(first, undefinedPoints.size()));
        }

        return runs;
    }

}
