package Controllers;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.TreeMap;

public class MapController {
    @FXML
    private MapView mapView;

    private DatabaseController databaseController;

    private TreeMap<String,Marker> marcadoresVehiculos;
    public MapController() {
        marcadoresVehiculos = new TreeMap<>();
        //DatabaseController databaseController = new DatabaseController();

        JSONObject jsonObject = databaseController.obtenerUbicacion();
        JSONArray jsonArray = (JSONArray) jsonObject.get("Ubicaciones");
        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            double latitud=Double.parseDouble(jsonObject1.get("latitud").toString());
            double longitud = Double.parseDouble(jsonObject1.get("longitud").toString());

            marcadoresVehiculos.put(jsonObject1.get("matricula").toString(),Marker.createProvided(Marker.Provided.BLUE).setPosition((new Coordinate(latitud,longitud))));
        }

        markerPuertoReal = Marker.createProvided(Marker.Provided.BLUE).setPosition(coordPuertoReal).setVisible(
                false);
        // no position for click marker yet
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(false);
    }

    public void initMapAndControls(DatabaseController databaseController,Projection projection){
        final OfflineCache offlineCache = mapView.getOfflineCache();
        this.databaseController = databaseController;
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });
        MapType mapType = MapType.OSM;

        mapView.setMapType(mapType);
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
    }

    private static final int ZOOM_DEFAULT = 14;
    private final Marker markerPuertoReal;
    private final Marker markerClick;
    private static final Coordinate coordPuertoReal = new Coordinate(36.5281889, -6.190111111111111);

    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            //labelEvent.setText("Event: map clicked at: " + newPosition);

            if (markerClick.getVisible()) {
                final Coordinate oldPosition = markerClick.getPosition();
                markerClick.setVisible(true);
                if (oldPosition != null) {
                    animateClickMarker(oldPosition, newPosition);
                } else {
                    markerClick.setPosition(newPosition);
                    // adding can only be done after coordinate is set
                    mapView.addMarker(markerClick);
                }
            }
        });
    }
    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition) {
        // animate the marker to the new position
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(1.0));
                setOnFinished(evt -> markerClick.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }
    private void afterMapIsInitialized() {

        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordPuertoReal);
        // add the markers to the map - they are still invisible
        mapView.addMarker(markerPuertoReal);

        for (String obj : marcadoresVehiculos.keySet()) {
            Marker marker = marcadoresVehiculos.get(obj);
            marker.setVisible(true);
            mapView.addMarker(marker);
            System.out.println(obj);
        }


    }
}
