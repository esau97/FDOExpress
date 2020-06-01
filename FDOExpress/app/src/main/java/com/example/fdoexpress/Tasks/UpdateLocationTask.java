package com.example.fdoexpress.Tasks;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class UpdateLocationTask extends AsyncTask<String,Void, String> {
    private boolean conectado;
    private String dir_ip="192.168.1.39";
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private static final int REQUEST_CODE_LOCATION=1;


    private Activity activity;
    private String location;

    public UpdateLocationTask(String location) {
        this.location=location;
    }

    @Override
    protected String doInBackground(String... strings) {
        conectado=true;
        try{
            this.socket=new Socket(dir_ip,4444);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String enviar="18&"+location;
            // TODO: Obtener ubicación actual y enviarla al servidor
            Log.i("Ubicacion",location);
            out.println(enviar);
            /*this.socket=new Socket(dir_ip,4444);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String argumentos [] = enviar.split("&");

            respuesta=in.readLine();
            accion=respuesta;*/
        } catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

}
