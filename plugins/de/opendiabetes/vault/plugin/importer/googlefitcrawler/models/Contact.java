package de.opendiabetes.vault.plugin.importer.googlefitcrawler.models;

import com.google.api.services.people.v1.model.Address;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.location.GooglePlaces;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Contact {
    private String name;
    private List<Address> address;
    private List<LatLng> coordinates;

    public Contact(String name) {
        this.name = name;
    }

    public Contact(String name, List<Address> address) {
        this.name = name;
        this.address = address;
        this.coordinates = getCoordinatesToAddress(address);
    }

    public Contact(String name, List<Address> address, List<LatLng> coordinates) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
    }

    private List<LatLng> getCoordinatesToAddress(List<Address> address) {
        return address.stream().map(a -> {
            GeocodingResult[] results = GooglePlaces.getInstance().addressToGPS(a.getFormattedValue());
            return new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
        }).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<Address> getAddress() {
        return address;
    }

    public Address getAddressById(int id){
        return address.get(id);
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public LatLng getCoordinateById(int id){
        return coordinates.get(id);
    }
}
