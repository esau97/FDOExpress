package com.example.fdoexpress.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.Utils.Codigos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainAsyncTask extends AsyncTask<String,Void, String> {

    private String dir_ip="192.168.1.39";

    private String enviar;
    private PeticionListener listener;
    private PrintWriter out;
    private Socket socket;
    private String accion;

    public MainAsyncTask(PeticionListener listener, String enviar) {
        this.enviar=enviar;
        this.listener=listener;

    }

    @Override
    protected String doInBackground(String... strings) {
        String respuesta="";
        try{
            this.socket=new Socket(dir_ip,4444);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(enviar);
            String argumentos [] = enviar.split("&");

            respuesta=in.readLine();
            accion=respuesta;

        } catch (SocketException e){
            accion="0&2";
            respuesta="0&";
        }catch (IOException e) {
            e.printStackTrace();
        }

        return respuesta;
    }


    @Override
    public void onPostExecute(String resultado){
        super.onPostExecute(resultado);
        listener.callback(accion);
    }
}
