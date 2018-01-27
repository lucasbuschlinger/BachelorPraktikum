package de.opendiabetes.vault.plugin.importer.googlecrawler.plot;

import com.google.maps.model.LatLng;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Constants;
import de.opendiabetes.vault.plugin.importer.googlecrawler.helper.Credentials;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapsPlot {
    private static GoogleMapsPlot instance;
    private List<LatLng> locations;
    private List<String> locationNames;
    private String htmlFile;

    public static GoogleMapsPlot getInstance() {
        if (GoogleMapsPlot.instance == null) {
            GoogleMapsPlot.instance = new GoogleMapsPlot();
        }
        return GoogleMapsPlot.instance;
    }

    public GoogleMapsPlot() {
        locations = new ArrayList<>();
        locationNames = new ArrayList<>();
    }

    public void addLocation(LatLng location) {
        locations.add(location);
    }

    public void addLocationNames(String name) {
        locationNames.add(name);
    }

    public void createMap() {
        htmlFile = Constants.MAPS_PRE;
        htmlFile += "var locations = [\n";
        for (int i = 0; i < locations.size(); i++) {
            htmlFile += "['" + locationNames.get(i) + "', " + locations.get(i).lat + ", " + locations.get(i).lng + ", " + String.valueOf(locations.size() - i) + "]";

            if(locations.size()-i != 1){
                htmlFile += ",\n";
            }else{
                htmlFile += "];\n";
            }
        }

        htmlFile += "var map = new google.maps.Map(document.getElementById('map'), {\n" +
                "      zoom: 12,\n" +
                "      center: new google.maps.LatLng(";
        htmlFile += String.valueOf(Math.round(locations.get(0).lat * 100.0) / 100.0) + ", " +String.valueOf(Math.round(locations.get(0).lng * 100.0) / 100.0)+"),\n";
        htmlFile += Constants.MAPS_POST;
        htmlFile += Credentials.getInstance().getAPIKey();
        htmlFile += Constants.MAPS_END;
    }

    public void openMap(){
        File file = new File("map.html");

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(htmlFile);
            writer.close();
            File htmlFile = new File("map.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
