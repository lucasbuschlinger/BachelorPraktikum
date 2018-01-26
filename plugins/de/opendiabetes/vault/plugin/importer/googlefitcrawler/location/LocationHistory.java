package de.opendiabetes.vault.plugin.importer.googlefitcrawler.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.helper.Constants;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.*;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.plot.GoogleMapsPlot;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LocationHistory {
    private static LocationHistory instance;

    private Map<Long, List<Coordinate>> locationHistory;
    private Map<Long, List<Activity>> activityHistory;
    private Map<Long, List<HeartRate>> heartRateHistory;
    private Map<Long, int[]> trainingHRHistory;

    private transient Map<ConflictedLocationIdentifier, List<PlacesSearchResult>> conflictLocations;

    private int age;

    private LocationHistory() {
        locationHistory = new HashMap<>();
        activityHistory = new HashMap<>();
        heartRateHistory = new HashMap<>();
        trainingHRHistory = new HashMap<>();
        conflictLocations = new HashMap<>();


        File file = new File(System.getProperty("user.home") + Constants.RESOLVED_LOCATION_PATH);
        if (file.exists() && !file.isDirectory()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                ResolvedLocations resLoc = gson.fromJson(new FileReader(file.getAbsolutePath().toString()), ResolvedLocations.class);
                ResolvedLocations.getInstance().importFromJson(resLoc);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    public static LocationHistory getInstance() {
        if (LocationHistory.instance == null) {
            LocationHistory.instance = new LocationHistory();
            GooglePlaces.getInstance().getOwnAddresses();
        }
        return LocationHistory.instance;
    }

    public List<Coordinate> getLocationsPerDay(long day) {
        day = normalizeDate(day);
        if (locationHistory.get(day) != null)
            return locationHistory.get(day);
        else
            return null;
    }

    public List<Coordinate> getLocationsForMultipleDays(long start, long end) {
        List<Coordinate> returnLocations = new ArrayList<>();
        start = normalizeDate(start);
        end = normalizeDate(end);

        do {
            if (getLocationsPerDay(start) != null)
                returnLocations.addAll(getLocationsPerDay(start));
            start += 86400000;
        } while (start <= end);

        return returnLocations;
    }

    public void addLocations(long day, List<Coordinate> locations) {
        this.locationHistory.put(normalizeDate(day), locations);
    }

    public List<Activity> getActivitiesPerDay(long day) {
        day = normalizeDate(day);
        if (locationHistory.get(day) != null)
            return activityHistory.get(day);
        else
            return null;
    }

    public List<Activity> getActivitiesForMultipleDays(long start, long end) {
        List<Activity> returnActivities = new ArrayList<>();
        start = normalizeDate(start);
        end = normalizeDate(end);

        do {
            if (getActivitiesPerDay(start) != null)
                returnActivities.addAll(getActivitiesPerDay(start));
            start += 86400000;
        } while (start <= end);

        return returnActivities;
    }


    public void addHeartRates(long day, List<HeartRate> heartRates) {
        this.heartRateHistory.put(normalizeDate(day), heartRates);
        this.trainingHRHistory.put(normalizeDate(day), determinRestHeartRate(day));
    }

    public List<HeartRate> getHeartRatesPerDay(long day) {
        day = normalizeDate(day);
        if (heartRateHistory.get(day) != null)
            return heartRateHistory.get(day);
        else
            return null;
    }

    public List<HeartRate> getHeartRatesForMultipleDays(long start, long end) {
        List<HeartRate> returnHeartRates = new ArrayList<>();
        start = normalizeDate(start);
        end = normalizeDate(end);

        do {
            if (getHeartRatesPerDay(start) != null)
                returnHeartRates.addAll(getHeartRatesPerDay(start));
            start += 86400000;
        } while (start <= end);

        return returnHeartRates;
    }


    public void addActivities(long day, List<Activity> activities) {
        this.activityHistory.put(normalizeDate(day), activities);
    }

    private long normalizeDate(long day) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private int[] determinRestHeartRate(long day) {
        int[] hr = new int[3];
        long time = -1;
        List<Activity> activities = getActivityHistory(day);

        for (Activity act : activities) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(act.getEndTime());
            if ((act.getActivity() == 72 || act.getActivity() == 109 || act.getActivity() == 110 || act.getActivity() == 111 || act.getActivity() == 112)
                    && cal.get(Calendar.HOUR) <= 12) {
                time = act.getEndTime();
            }
        }

        final long wakeUpTime = time;
        List<Integer> rates = new ArrayList<>();

        if (time == -1) {
            getHeartRateHistory(day).forEach(r -> rates.add(r.getRate()));
        } else {
            getHeartRateHistory(day).forEach(r -> {
                if (r.getTimestamp() < wakeUpTime + 300000 && r.getTimestamp() > wakeUpTime - 300000)
                    rates.add(r.getRate());
            });
        }

        Collections.sort(rates);

        double quantilePos = (rates.size() * 0.3);

        if (quantilePos % 1 == 0) {
            int a = rates.get((int) quantilePos - 1);
            int b = rates.get((int) quantilePos);
            hr[Constants.REST_HR] = (int) (0.5 * (a + b)); // 0,5*(x(np)*x(np+1))
        } else {
            int pos = (int) Math.floor(quantilePos); // floor(np) + 1
            hr[Constants.REST_HR] = rates.get(pos);
        }

        hr[Constants.MAX_HR] = 220 - age; //getHeartRateHistory(day).stream().max(Comparator.comparing(HeartRate::getRate)).get().getRate();
        hr[Constants.TARGET_HR] = hr[Constants.MAX_HR] - hr[Constants.REST_HR];

        return hr;
    }

    public void refineLocations() {
        for (Map.Entry<Long, List<Activity>> entry : activityHistory.entrySet()) {
            List<Coordinate> coords = getLocationsPerDay(entry.getKey());
            for (Activity act : entry.getValue()) {
                if (act.getActivity() == 3 || act.getActivity() == 4 || act.getActivity() == 72 || act.getActivity() == 109 || act.getActivity() == 110 || act.getActivity() == 111 || act.getActivity() == 45) {
                    List<Coordinate> activityCoords = new ArrayList<>();
                    long startTime = act.getStartTime();
                    long endTime = act.getEndTime();


                    for (Coordinate c : coords) {
                        if (c.getTimestamp() >= startTime && c.getTimestamp() <= endTime)
                            activityCoords.add(c);
                    }

                    if (activityCoords.size() > 0) {
                        activityCoords.sort(Comparator.comparing(Coordinate::getAccuracy));
                        int threshold = activityCoords.get((int) (activityCoords.size() * 0.75)).getAccuracy();

                        activityCoords = activityCoords.stream().filter(c -> c.getAccuracy() <= threshold).collect(Collectors.toList());

                        double weightedLatitude = 0;
                        double weightedLongitude = 0;
                        double weight = 0;

                        for (Coordinate c : activityCoords) {
                            weightedLatitude += c.getLatitude() * c.getAccuracy();
                            weightedLongitude += c.getLongitude() * c.getAccuracy();
                            weight += c.getAccuracy();
                        }

                        final double lat = weightedLatitude / weight;
                        final double lng = weightedLongitude / weight;

                        int searchRadius = 50;
                        if (searchRadius < threshold)
                            searchRadius = threshold;

                        String place = GooglePlaces.getInstance().atOwnPlaces(lat, lng, searchRadius);

                        if (place.equals("AWAY")) {
                            place = GooglePlaces.getInstance().isAtContact(lat, lng, searchRadius);

                            if (place.equals("AWAY")) {

                                PlacesSearchResult[] results = GooglePlaces.getInstance().getPlaces(lat, lng, searchRadius);

                                if (results != null) {
                                    if (results.length == 1) {
                                        place = results[0].name;
                                    } else {
                                        List<PlacesSearchResult> places = extractLocations(results);
                                        places.sort((PlacesSearchResult o1, PlacesSearchResult o2) -> {
                                            double o1Distance = GooglePlaces.getInstance().calculateDistance(o1.geometry.location.lat, o1.geometry.location.lng, lat, lng);
                                            double o2Distance = GooglePlaces.getInstance().calculateDistance(o2.geometry.location.lat, o2.geometry.location.lng, lat, lng);
                                            return Double.compare(o1Distance, o2Distance);
                                        });

                                        List<PlacesSearchResult> sportRelatedPlaces = isGymOrSportClub(places);
                                        GoogleMapsPlot.getInstance().addLocation(new LatLng(lat, lng));

                                        if (sportRelatedPlaces.size() == 1) {
                                            place = sportRelatedPlaces.get(0).name;
                                        } else {
                                            Location location = checkForResolvedLocations(lat, lng, searchRadius*2);
                                            if (location != null) {
                                                place = location.name;
                                            } else if (sportRelatedPlaces.size() > 1) {
                                                conflictLocations.put(new ConflictedLocationIdentifier(act.getStartTime(), new LatLng(lat, lng)), sportRelatedPlaces);
                                                place = "CONFLICT";
                                            } else if (places.size() > 0) {
                                                results = GooglePlaces.getInstance().getPlaces(lat, lng, searchRadius * 2);
                                                places = extractLocations(results);
                                                places.sort((PlacesSearchResult o1, PlacesSearchResult o2) -> {
                                                    double o1Distance = GooglePlaces.getInstance().calculateDistance(o1.geometry.location.lat, o1.geometry.location.lng, lat, lng);
                                                    double o2Distance = GooglePlaces.getInstance().calculateDistance(o2.geometry.location.lat, o2.geometry.location.lng, lat, lng);
                                                    return Double.compare(o1Distance, o2Distance);
                                                });

                                                conflictLocations.put(new ConflictedLocationIdentifier(act.getStartTime(), new LatLng(lat, lng)), places);

                                                place = "CONFLICT";
                                            } else {
                                                place = "UNKNOWN";
                                            }

                                        }

                                    }
                                } else {
                                    place = "UNKNOWN";
                                }
                            }
                        }
                        act.setLocation(place);
                    }

                    GoogleMapsPlot.getInstance().addLocationNames(act.getLocation());
                }
            }
        }
    }

    private Location checkForResolvedLocations(double lat, double lng, int searchRadius) {
        List<Location> locations = ResolvedLocations.getInstance().getLocations();
        if (!locations.isEmpty()) {
            for (Location loc : locations) {
                if (GooglePlaces.getInstance().calculateDistance(loc.coordinate.lat, loc.coordinate.lng, lat, lng) <= searchRadius)
                    return loc;
            }
        }

        return null;
    }

    private List<PlacesSearchResult> isGymOrSportClub(List<PlacesSearchResult> places) {
        List<PlacesSearchResult> sportRelatedPlaces = new ArrayList<>();

        for (PlacesSearchResult sr : places) {
            boolean isSportRelated = false;
            for (String st : sr.types) {
                if (st.equals("gym"))
                    isSportRelated = true;
            }

            if (isSportRelated)
                sportRelatedPlaces.add(sr);
        }

        for (PlacesSearchResult sr : places) {
            if (sr.name.toLowerCase().contains("verein") || sr.name.toLowerCase().contains("club") || sr.name.toLowerCase().contains("bad"))
                sportRelatedPlaces.add(sr);
        }


        return sportRelatedPlaces;
    }


    private List<PlacesSearchResult> extractLocations(PlacesSearchResult[] places) {
        List<PlacesSearchResult> results = new ArrayList<>();

        for (PlacesSearchResult sr : places) {
            boolean isPolitical = false;
            for (String st : sr.types) {
                if (st.toLowerCase().equals("political") || st.toLowerCase().equals("route") ||
                        st.toLowerCase().equals("locality") || st.toLowerCase().equals("street_address"))
                    isPolitical = true;
            }

            if (!isPolitical)
                results.add(sr);
        }

        return results;
    }

    public void determinActivityIntensity() {
        for (Map.Entry<Long, List<Activity>> entry : activityHistory.entrySet()) {
            long day = entry.getKey();
            for (Activity act : entry.getValue()) {
                if (act.getEndTime() - act.getStartTime() >= 1200000) {
                    act.setHeartRate(getMinMaxAvgHeartRate(act.getStartTime(), act.getEndTime()));
                    if (act.getHeartRate()[Constants.MIN_HR] != -1) {
                        int[] trainingHR = trainingHRHistory.get(day);

                        if ((0.6 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] <= act.getHeartRate()[Constants.AVG_HR] && (0.7 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] > act.getHeartRate()[Constants.AVG_HR])
                            act.setIntensity(1);
                        else if ((0.7 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] <= act.getHeartRate()[Constants.AVG_HR] && (0.75 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] >= act.getHeartRate()[Constants.AVG_HR])
                            act.setIntensity(2);
                        else if ((0.75 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] < act.getHeartRate()[Constants.AVG_HR] && (0.84 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] >= act.getHeartRate()[Constants.AVG_HR])
                            act.setIntensity(3);
                        else if ((0.84 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] < act.getHeartRate()[Constants.AVG_HR] && (0.88 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] >= act.getHeartRate()[Constants.AVG_HR])
                            act.setIntensity(4);
                        else if ((0.88 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] < act.getHeartRate()[Constants.AVG_HR] && (0.92 * trainingHR[Constants.TARGET_HR]) + trainingHR[Constants.REST_HR] >= act.getHeartRate()[Constants.AVG_HR])
                            act.setIntensity(5);
                        else
                            act.setIntensity(0);
                    }
                }
            }
        }
    }

    private int[] getMinMaxAvgHeartRate(long start, long end) {
        int[] heartRate = new int[]{-1, -1, -1};
        List<Integer> rates = new ArrayList<>();

        if (getHeartRateHistory(start) != null) {
            getHeartRateHistory(start).forEach(r -> {
                if (r.getTimestamp() < start && r.getTimestamp() > end)
                    rates.add(r.getRate());
            });


            heartRate[Constants.MAX_HR] = rates.stream().max(Comparator.naturalOrder()).get();
            heartRate[Constants.MIN_HR] = rates.stream().min(Comparator.reverseOrder()).get();
            heartRate[Constants.AVG_HR] = (int) rates.stream().mapToInt(Integer::intValue).average().getAsDouble();
        }

        return heartRate;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void export() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(this);

        File file = new File("activity_location_history_" + activityHistory.keySet().toArray()[0] + "-" + activityHistory.keySet().toArray()[activityHistory.size() - 1] + ".json");

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Activity> getActivityHistory(long day) {
        return activityHistory.get(normalizeDate(day));
    }

    public List<HeartRate> getHeartRateHistory(long day) {
        return heartRateHistory.get(normalizeDate(day));
    }

    public Map<ConflictedLocationIdentifier, List<PlacesSearchResult>> getConflictedActivities() {
        return conflictLocations;
    }

}

