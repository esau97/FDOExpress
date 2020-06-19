package Controllers;

import Util.Preferencias;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TreeMap;

public class MapController {
    @FXML
    private MapView mapView;
    private boolean connected;

    private Thread hiloControlador;

    private DatabaseController databaseController;

    @FXML
    private JFXCheckBox cbTodos;

    @FXML
    private FontAwesomeIcon imageBuscar;

    @FXML
    private JFXTextField textMatricula;

    private TreeMap<String,Marker> marcadoresVehiculos;
    private TreeMap<String,MapLabel> labelVehiculos;


    public MapController() {
        marcadoresVehiculos = new TreeMap<>();
        labelVehiculos = new TreeMap<>();
        //DatabaseController databaseController = new DatabaseController(new Preferencias("192.168.137.123"));
    }

    public void initMapAndControls(DatabaseController databaseController,Projection projection){
        final OfflineCache offlineCache = mapView.getOfflineCache();
        this.databaseController = databaseController;

        /*JSONObject jsonObject = databaseController.obtenerUbicacion();
        JSONArray jsonArray = (JSONArray) jsonObject.get("Ubicaciones");
        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            double latitud=Double.parseDouble(jsonObject1.get("latitud").toString());
            double longitud = Double.parseDouble(jsonObject1.get("longitud").toString());

            marcadoresVehiculos.put(jsonObject1.get("matricula").toString(),Marker.createProvided(Marker.Provided.BLUE).setPosition((new Coordinate(latitud,longitud))));
        }*/


        hiloControlador =
        new Thread(){
            private PrintWriter out;
            @Override
            public void run(){
                connected = true;
                JSONObject jsonObject = new JSONObject();
                Coordinate nuevaPosicion;
                try{
                    Socket socket = new Socket(databaseController.getPref().getDir_ip(),4444);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("7");
                    int cont=0;

                    while (connected){
                        System.out.println("en bucle");
                        String recibido = in.readLine();
                        String argumentos [] = recibido.split("&");
                        JSONParser jsonParser = new JSONParser();
                        jsonObject = (JSONObject) jsonParser.parse(argumentos[1]);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Ubicaciones");
                        System.out.println(jsonObject);
                        for (int i = 0; i < jsonArray.size() ; i++) {
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                            double latitud=Double.parseDouble(jsonObject1.get("latitud").toString());
                            double longitud = Double.parseDouble(jsonObject1.get("longitud").toString());
                            nuevaPosicion = new Coordinate(latitud,longitud);
                            if(cont!=0){
                                animateClickMarker(marcadoresVehiculos.get(jsonObject1.get("matricula").toString()).getPosition(),nuevaPosicion,marcadoresVehiculos.get(jsonObject1.get("matricula")));
                            }else{
                                marcadoresVehiculos.put(jsonObject1.get("matricula").toString(),Marker.createProvided(Marker.Provided.BLUE).setPosition((new Coordinate(latitud,longitud))));
                            }
                            //marcadoresVehiculos.get(jsonObject1.get("matricula").toString()).setPosition(nuevaPosicion);
                            //marcadoresVehiculos.put(jsonObject1.get("matricula").toString(),Marker.createProvided(Marker.Provided.BLUE).setPosition((new Coordinate(latitud,longitud))));
                        }
                        if (cont==0){
                            addMarkers();
                            cont++;
                        }
                        Thread.sleep(20000);
                        out.println("7");
                    }
                    // Envío el código 0 para cuando se termine la conexión
                    out.println("0");
                    socket.close();
                    in.close();
                    out.close();
                } catch (InterruptedException e) {
                    connected=false;
                    out.println("0");
                    System.out.println("Cerrando hilo actualizar ubicación.");
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            public void addMarkers(){
                System.out.println("Añadiendo marcadores");
                MapLabel mapLabel;
                for (String obj : marcadoresVehiculos.keySet()) {
                    Marker marker = marcadoresVehiculos.get(obj);
                    marker.setVisible(true);
                    mapView.addMarker(marker);
                    mapLabel=new MapLabel(obj,10, -10).setVisible(true).setCssClass("orange-label");
                    labelVehiculos.put(obj,mapLabel);
                    marker.attachLabel(mapLabel);
                    System.out.println(obj);
                }
            }
        };
        hiloControlador.start();

        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });
        MapType mapType = MapType.OSM;


        cbTodos.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    for (String clave : marcadoresVehiculos.keySet()) {
                        marcadoresVehiculos.get(clave).setVisible(true);
                        labelVehiculos.get(clave).setVisible(true);
                    }
                }else{
                    for (String clave : marcadoresVehiculos.keySet()) {
                        marcadoresVehiculos.get(clave).setVisible(false);
                        labelVehiculos.get(clave).setVisible(false);
                    }
                }
            }
        });
        mapView.setMapType(mapType);
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
    }

    private static final int ZOOM_DEFAULT = 10;

    private static final Coordinate coordPuertoReal = new Coordinate(36.5281889, -6.190111111111111);

    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition, Marker marcador) {
        // animate the marker to the new position
        System.out.println("Cambiando posicion");
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(1.0));
                setOnFinished(evt -> marcador.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                marcador.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }
    private void afterMapIsInitialized() {

        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordPuertoReal);
        // add the markers to the map - they are still invisible
        cbTodos.setDisable(false);

        for (String obj : marcadoresVehiculos.keySet()) {
            Marker marker = marcadoresVehiculos.get(obj);
            marker.setVisible(true);
            labelVehiculos.get(obj).setVisible(true);
            mapView.addMarker(marker);
            System.out.println(obj);
        }


    }

    public void shutdown(){
        // Método que se ejecuta cuando la ventana ha sido cerrada
        connected=false;
        // Interrumpo el hilo que se encargaba de la conexión con el servidor para solicitar los datos de la nueva
        // ubicación de cada vehículo.
        hiloControlador.interrupt();
    }

    public void buscarVehiculo(){
        cbTodos.selectedProperty().setValue(false);
        String matricula = textMatricula.getText();
        Marker marcador;
        if(!matricula.equals("") && marcadoresVehiculos.containsKey(matricula)){
            //cbTodos.selectedProperty().setValue(false);
            for (String clave : marcadoresVehiculos.keySet()) {
                marcadoresVehiculos.get(clave).setVisible(false);
            }
            marcador = marcadoresVehiculos.get(matricula);
            marcador.setVisible(true);
            mapView.setCenter(marcador.getPosition());
            mapView.setZoom(ZOOM_DEFAULT);
        }
    }

}
