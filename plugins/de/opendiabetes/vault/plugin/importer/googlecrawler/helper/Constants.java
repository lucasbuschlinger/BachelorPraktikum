package de.opendiabetes.vault.plugin.importer.googlecrawler.helper;

public class Constants {
    public static final int REST_HR = 0;
    public static final int TARGET_HR = 1;
    public static final int MAX_HR = 2;

    public static final int MIN_HR = 0;
    public static final int AVG_HR = 1;

    public static final String RESOLVED_LOCATION_PATH = "/.credentials/googleapis.de-nkpyck-googledatagatherer/resolved_Locations.json";

    public static final String MAPS_PRE =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <style>\n" +
                    "      /* Always set the map height explicitly to define the size of the div\n" +
                    "       * element that contains the map. */\n" +
                    "      #map {\n" +
                    "        height: 100%;\n"+
                    "        width: 100%;\n"+
                    "      }\n" +
                    "      /* Optional: Makes the sample page fill the window. */\n" +
                    "      html, body {\n" +
                    "        height: 100%;\n" +
                    "        margin: 0;\n" +
                    "        padding: 0;\n" +
                    "      }\n" +
                    "    </style>\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <div id=\"map\"></div>\n" +
                    "    <script>\n" +
                    "      function initMap() {\n";

    public static final String MAPS_POST =
            "      mapTypeId: google.maps.MapTypeId.ROADMAP\n" +
                    "    });\n" +
                    "\n" +
                    "    var infowindow = new google.maps.InfoWindow();\n" +
                    "\n" +
                    "    var marker, i;\n" +
                    "\n" +
                    "    for (i = 0; i < locations.length; i++) {  \n" +
                    "      marker = new google.maps.Marker({\n" +
                    "        position: new google.maps.LatLng(locations[i][1], locations[i][2]),\n" +
                    "        map: map\n" +
                    "      });\n" +
                    "\n" +
                    "      google.maps.event.addListener(marker, 'click', (function(marker, i) {\n" +
                    "        return function() {\n" +
                    "          infowindow.setContent(locations[i][0]);\n" +
                    "          infowindow.open(map, marker);\n" +
                    "        }\n" +
                    "      })(marker, i));\n" +
                    "    }}\n" +
                    "  </script>\n" +
                    "  <script async defer \n"+
                    "  src=\"https://maps.googleapis.com/maps/api/js?key=";

    public static final String MAPS_END =
            "&callback=initMap\">\n"+
                    "  </script>\n"+
                    "</body>\n" +
                    "</html>";

}
