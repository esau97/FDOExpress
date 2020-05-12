package com.example.fdoexpress.ui.home;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.OrderLocation;
import com.example.fdoexpress.Utils.Codigos;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HomeMapFragment extends Fragment implements OnMapReadyCallback {
    private String codigoPedido;
    private GoogleMap mMap;
    private MapView mapView;
    private View mView;
    private Marker orderMarker;
    private OrderLocation orderLocation;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView =  inflater.inflate(R.layout.fragment_home_second, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) mView.findViewById(R.id.mapView);
        if(mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        //Obtengo el codigo del pedido. Ahora debería inicar una petición en la que
        // indico el código del pedido y me devuelven la geolocalización del
        //pedido
        String myArg = HomeMapFragmentArgs.fromBundle(getArguments()).getMyArg();
        codigoPedido=myArg;

    }

    public void tratarMensaje(String recibido){
        String argumentos[]=recibido.split("&");
        int cod=Integer.parseInt(argumentos[0]);
        switch(Codigos.codigo_cliente(cod)){
            case NUEVA_UBICACION:
                JSONObject jsonObject;
                JSONParser jsonParser = new JSONParser();
                try{
                    jsonObject = (JSONObject) jsonParser.parse(argumentos[1]);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("Ubicaciones");
                    //System.out.println(jsonObject);
                    double latitud=0,longitud=0;
                    for (int i = 0; i < jsonArray.size() ; i++) {
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                        latitud=Double.parseDouble(jsonObject1.get("latitud").toString());
                        longitud = Double.parseDouble(jsonObject1.get("longitud").toString());
                    }
                    cambiarUbicacion(latitud,longitud);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }


    }
    public void cambiarUbicacion(double latitud,double longitud){

        if (orderMarker!=null && latitud!=0 && longitud!=0){
            LatLng newLatLong = new LatLng(latitud,longitud);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLong));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLong, 12.0f));
            orderMarker.setPosition(newLatLong);
        }else if (orderMarker==null){
            LatLng newLatLong = new LatLng(latitud,longitud);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLong));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLong, 12.0f));
            MarkerOptions mk = new MarkerOptions().position(newLatLong).title("Marker in Puerto Real");
            orderMarker = mMap.addMarker(mk);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        // Add a marker in Puerto Real and move the camera
        LatLng puertoReal = new LatLng(36.5281889000, -6.1901111111);
        MarkerOptions mk = new MarkerOptions().position(puertoReal).title("Marker in Puerto Real");
        orderMarker = mMap.addMarker(mk);


        orderLocation = new OrderLocation(new PeticionListener() {
            @Override
            public void callback(final String accion) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tratarMensaje(accion);
                    }
                });
            }
        },codigoPedido);
        orderLocation.start();

    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                // TODO: Añadir funcionalidad al botón backpressed
                orderLocation.interrupt();
                Toast.makeText(context, "Back pulsado", Toast.LENGTH_SHORT).show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }
}
