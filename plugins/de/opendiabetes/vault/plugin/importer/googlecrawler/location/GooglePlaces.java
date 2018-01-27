package de.opendiabetes.vault.plugin.importer.googlecrawler.location;

import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.Person;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Credentials;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.Contact;
import de.opendiabetes.vault.plugin.importer.googlecrawler.people.AddressBook;
import de.opendiabetes.vault.plugin.importer.googlecrawler.people.GooglePeople;
import de.opendiabetes.vault.plugin.importer.googlecrawler.plot.GoogleMapsPlot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GooglePlaces {
    private static GooglePlaces instance;

    private final static long EARTH_RADIUS_METERS = 6371000;

    private GeoApiContext context;

    private String keywordSearch = "(verein) OR (sport) OR (bad)";

    private List<Contact> ownAddresses;

    public static GooglePlaces getInstance() {
        if (GooglePlaces.instance == null) {
            GooglePlaces.instance = new GooglePlaces();
        }
        return GooglePlaces.instance;
    }

    public GooglePlaces() {
        ownAddresses = new ArrayList<>();
        construct(Credentials.getInstance().getAPIKey());
    }

    private void construct(String apiKey) {
        context = new GeoApiContext.Builder()
                .apiKey(apiKey).queryRateLimit(100)
                .build();
    }


    public GeocodingResult[] GPStoAddress(double latitude, double longitude) {
        if (context == null)
            return null;

        try {
            return GeocodingApi.reverseGeocode(context, new LatLng(latitude,
                    longitude)).await();

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeocodingResult[] addressToGPS(String address) {
        if (context == null)
            return null;

        try {
            return GeocodingApi.geocode(context, address).await();

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isGym(double latitude, double longitude) {
        try {
            PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, new LatLng(latitude,
                    longitude)).radius(50).type(PlaceType.GYM).await().results;

            if (results.length > 0)
                return true;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setKeywordSearchParams(String[] keywordSearchParams) {
        for (String param : keywordSearchParams) {
            keywordSearch = keywordSearch + " OR (" + param.toLowerCase() + ")";
        }
    }

    public String keywordSearch(double latitude, double longitude, int accuracy) {
        try {
            PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, new LatLng(latitude, longitude)).radius(accuracy).keyword(keywordSearch).await().results;
            if (results.length == 1) {
                return results[0].name;
            } else {
                for (PlacesSearchResult sr : results) {
                    boolean isPolitical = false;
                    for (String st : sr.types) {
                        if (st.equals("political"))
                            isPolitical = true;
                    }

                    if (!isPolitical)
                        return sr.name;
                }
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return "AWAY";
    }

    public void getOwnAddresses() {
        if (context != null) {
            Person me = GooglePeople.getInstance().getProfile();


            if (me.getAddresses() != null) {

                for (Address res : me.getAddresses()) {
                    String name = res.getFormattedType();
                    List<Address> address = Arrays.asList(res);
                    ownAddresses.add(new Contact(name, address));
                }
            }

        }
    }

    public String atOwnPlaces(double lat, double lng, int acc) {
        for (Contact ad : ownAddresses) {
            if (calculateDistance(lat, lng, ad.getCoordinateById(0).lat, ad.getCoordinateById(0).lng) <= acc) {
                GoogleMapsPlot.getInstance().addLocation(new LatLng(ad.getCoordinateById(0).lat, ad.getCoordinateById(0).lng));
                return ad.getName();
            }

        }
        return "AWAY";
    }

    public PlacesSearchResult[] getPlaces(double lat, double lng, int acc) {
        try {
            return PlacesApi.nearbySearchQuery(context, new LatLng(lat, lng)).radius(acc).await().results;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFoodLocation(double lat, double lng) {
        try {
            PlacesSearchResult[] results = PlacesApi.nearbySearchQuery(context, new LatLng(lat, lng)).radius(50).type(PlaceType.RESTAURANT, PlaceType.FOOD, PlaceType.CAFE, PlaceType.MEAL_TAKEAWAY).await().results;
            if (results.length > 0) {
                return results[0].name;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return "AWAY";
    }

    public String isAtContact(double lat, double lng, int searchRadius) {
        for (int i = 0; i < AddressBook.getInstance().size(); i++) {
            Contact ref = AddressBook.getInstance().getContactById(i);
            for (LatLng ll : ref.getCoordinates()) {
                if (GooglePlaces.getInstance().calculateDistance(lat, lng, ll.lat, ll.lng) < searchRadius) {
                    GoogleMapsPlot.getInstance().addLocation(new LatLng(ll.lat, ll.lng));
                    return ref.getName();
                }
            }

        }

        return "AWAY";
    }

    public double calculateDistance(double lat, double lng, double latRef, double lngRef) {
        double latRefRadian = Math.toRadians(latRef);

        double latOneRadian = Math.toRadians(lat);

        double deltaLat = Math.toRadians(lat - latRef);
        double deltaLong = Math.toRadians(lng - lngRef);

        double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                + Math.cos(latRefRadian) * Math.cos(latOneRadian)
                * Math.sin(deltaLong / 2.0) * Math.sin(deltaLong / 2.0);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }


}
