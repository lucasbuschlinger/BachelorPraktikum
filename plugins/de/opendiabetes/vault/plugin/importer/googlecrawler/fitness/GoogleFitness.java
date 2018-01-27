package de.opendiabetes.vault.plugin.importer.googlecrawler.fitness;

import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.AggregateBy;
import com.google.api.services.fitness.model.AggregateRequest;
import com.google.api.services.fitness.model.AggregateResponse;
import com.google.api.services.fitness.model.DataPoint;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Credentials;
import de.opendiabetes.vault.plugin.importer.googlecrawler.location.LocationHistory;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.Activity;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.Coordinate;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.HeartRate;

import java.io.IOException;
import java.util.*;

public class GoogleFitness {
    private static GoogleFitness instance;

    private Fitness fitnessService;

    private GoogleFitness() {
        construct();
    }

    public static GoogleFitness getInstance() {
        if (GoogleFitness.instance == null) {
            GoogleFitness.instance = new GoogleFitness();
        }
        return GoogleFitness.instance;
    }


    private void construct() {
        fitnessService = new Fitness.Builder(Credentials.getInstance().getHttpTransport(), Credentials.getInstance().getJsonFactory(), Credentials.getInstance().getCredential())
                .setApplicationName(Credentials.getInstance().getApplicationName())
                .build();
    }

    public void getData(long start, long end) {
        while (start <= end) {
            getActivitiesPerDay(start);
            getLocationsPerDay(start);
            getHearRatePerDay(start);
            start += 86400000;
        }

        LocationHistory.getInstance().refineLocations();
        LocationHistory.getInstance().determinActivityIntensity();
    }

    public void getActivitiesPerDay(long day) {
        long[] startEnd = getStartEndDay(day);

        AggregateBy aggregate = new AggregateBy();
        aggregate.setDataTypeName("com.google.activity.segment");

        AggregateRequest agg = new AggregateRequest();
        agg.setStartTimeMillis(startEnd[0]);
        agg.setEndTimeMillis(startEnd[1]);
        agg.setAggregateBy(Arrays.asList(aggregate));

        try {
            Fitness.Users.Dataset.Aggregate request = fitnessService.users().dataset().aggregate("me", agg);
            AggregateResponse rep = request.execute();

            List<DataPoint> activitiesByDataSource = rep.getBucket().get(0).getDataset().get(0).getPoint();
            List<Activity> activities = new ArrayList<>();
            for (DataPoint dp : activitiesByDataSource) {
                activities.add(new Activity(dp.getStartTimeNanos() / 1000000, dp.getEndTimeNanos() / 1000000, dp.getValue().get(0).getIntVal()));
            }

            for (int i = 0; i < activities.size() - 1; i++){
                if(activities.get(i).getActivity() == activities.get(i+1).getActivity() && activities.get(i).getEndTime()-activities.get(i+1).getStartTime() <= 300000){
                    activities.get(i).setEndTime(activities.get(i+1).getEndTime());
                    activities.remove(i+1);
                }
            }


            LocationHistory.getInstance().addActivities(startEnd[0], activities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getLocationsPerDay(long day) {
        long[] startEnd = getStartEndDay(day);

        AggregateBy aggregate = new AggregateBy();
        aggregate.setDataTypeName("com.google.location.sample");

        AggregateRequest agg = new AggregateRequest();
        agg.setStartTimeMillis(startEnd[0]);
        agg.setEndTimeMillis(startEnd[1]);
        agg.setAggregateBy(Arrays.asList(aggregate));

        try {
            Fitness.Users.Dataset.Aggregate request = fitnessService.users().dataset().aggregate("me", agg);
            AggregateResponse rep = request.execute();

            List<DataPoint> activitiesByDataSource = rep.getBucket().get(0).getDataset().get(0).getPoint();
            List<Coordinate> locations = new ArrayList<>();
            for (DataPoint dp : activitiesByDataSource) {
                Coordinate coord = new Coordinate(dp.getStartTimeNanos() / 1000000, dp.getValue().get(1).getFpVal(), dp.getValue().get(0).getFpVal(), (int) Math.ceil(dp.getValue().get(2).getFpVal()));
                if (dp.getValue().size() > 3)
                    coord.setAltitude(dp.getValue().get(3).getFpVal().intValue());
                locations.add(coord);
            }

            LocationHistory.getInstance().addLocations(startEnd[0], locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getHearRatePerDay(long day) {
        long[] startEnd = getStartEndDay(day);

        AggregateBy aggregate = new AggregateBy();
        aggregate.setDataTypeName("com.google.heart_rate.bpm");

        AggregateRequest aggregateRequest = new AggregateRequest();
        aggregateRequest.setStartTimeMillis(startEnd[0]);
        aggregateRequest.setEndTimeMillis(startEnd[1]);
        aggregateRequest.setAggregateBy(Arrays.asList(aggregate));

        try {
            Fitness.Users.Dataset.Aggregate request = fitnessService.users().dataset().aggregate("me", aggregateRequest);
            AggregateResponse rep = request.execute();

            if (rep.getBucket().get(0).getDataset().get(0).getPoint().size() != 0) {
                List<DataPoint> activitiesByDataSource = rep.getBucket().get(0).getDataset().get(0).getPoint();
                List<HeartRate> heartRates = new ArrayList<>();
                for (DataPoint dp : activitiesByDataSource) {
                    heartRates.add(new HeartRate(dp.getStartTimeNanos() / 1000000, dp.getValue().get(0).getIntVal()));
                }

                LocationHistory.getInstance().addHeartRates(startEnd[0], heartRates);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long[] getStartEndDay(long day) {
        long[] startEnd = new long[2];

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        startEnd[0] = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        startEnd[1] = cal.getTimeInMillis();

        return startEnd;
    }
}
