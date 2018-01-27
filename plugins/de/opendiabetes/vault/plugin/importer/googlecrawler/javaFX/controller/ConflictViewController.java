package de.opendiabetes.vault.plugin.importer.googlecrawler.javaFX.controller;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Credentials;
import de.opendiabetes.vault.plugin.importer.googlecrawler.location.LocationHistory;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.ConflictedLocationIdentifier;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.Location;
import de.opendiabetes.vault.plugin.importer.googlecrawler.models.ResolvedLocations;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ConflictViewController {
    @FXML
    private ListView conflictedActivitiesListView;

    @FXML
    private ListView conflictedLocationsListView;

    @FXML
    private WebView conflictedLocationWebView;

    @FXML
    private TextField conflictedLocationTextField;

    private Map<ConflictedLocationIdentifier, List<PlacesSearchResult>> conflictedActivities;

    public ConflictViewController() {
    }


    @FXML
    private void initialize() {
        conflictedActivities = LocationHistory.getInstance().getConflictedActivities();
        setConflictedActivitiesListView();
    }

    private void setConflictedActivitiesListView() {
        if (!conflictedActivities.isEmpty()) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            List<String> values = conflictedActivities.entrySet().stream().map(e -> {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(e.getKey().timestamp);
                return timeFormat.format(cal.getTime());
            }).collect(Collectors.toList());

            conflictedActivitiesListView.setItems(FXCollections.observableList(values));
            conflictedActivitiesListView.getSelectionModel().selectFirst();
            onMouseClickedActivity(null);
        }else{
            conflictedActivitiesListView.getItems().clear();
        }
    }

    private List<PlacesSearchResult> places;
    private Object selectedActivityKey;

    @FXML
    public void onMouseClickedActivity(MouseEvent event) {
        selectedActivityKey = (conflictedActivities.keySet().toArray())[conflictedActivitiesListView.getSelectionModel().getSelectedIndex()];
        places = conflictedActivities.get(selectedActivityKey);

        List<String> values = places.stream().map(p -> p.name).collect(Collectors.toList());
        conflictedLocationsListView.setItems(FXCollections.observableList(values));

        StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?zoom=18&size=400x500&maptype=roadmap");

        List<String> colors = new ArrayList<>();
        colors.addAll(Arrays.asList("blue", "green", "red", "yellow"));

        urlBuilder.append("&center=").append(places.get(0).geometry.location.lat).append(",").append(places.get(0).geometry.location.lng);

        for (int i = 0; i < places.size(); i++) {
            urlBuilder.append("&markers=color:").append(colors.get(i % colors.size()));
            urlBuilder.append("%7Clabel:").append(places.get(i).name.substring(0, 1));
            urlBuilder.append("%7C").append(places.get(i).geometry.location.lat).append(",").append(places.get(i).geometry.location.lng);
        }

        urlBuilder.append("&key=").append(Credentials.getInstance().getAPIKey());


        WebEngine webEngine = conflictedLocationWebView.getEngine();
        webEngine.load(urlBuilder.toString());
    }

    @FXML
    public void saveSelectedLocation(MouseEvent event) {
        if (!conflictedLocationsListView.getSelectionModel().isEmpty()) {
            Location loc = new Location();
            PlacesSearchResult sr = places.get(conflictedLocationsListView.getSelectionModel().getSelectedIndex());
            loc.name = sr.name;
            loc.coordinate = new LatLng(sr.geometry.location.lat, sr.geometry.location.lng);

            ResolvedLocations.getInstance().addLocation(loc);

            conflictedActivities.remove(selectedActivityKey);

            conflictedLocationsListView.getItems().clear();
            conflictedLocationWebView.getEngine().load("");

            setConflictedActivitiesListView();
        }
    }

    @FXML
    public void saveCustomLabel(MouseEvent event) {
        if (!conflictedLocationTextField.getText().isEmpty()) {
            Location loc = new Location();
            loc.name = conflictedLocationTextField.getText();
            loc.coordinate = ((ConflictedLocationIdentifier) selectedActivityKey).coordinate;

            ResolvedLocations.getInstance().addLocation(loc);

            conflictedActivities.remove(selectedActivityKey);

            conflictedLocationsListView.getItems().clear();
            conflictedLocationWebView.getEngine().load("");

            setConflictedActivitiesListView();
        }
    }
}
