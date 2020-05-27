package com.example.fdoexpress.Tasks;

import android.os.AsyncTask;
import com.example.fdoexpress.PeticionListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryTask extends AsyncTask<String,Void, String> {

    byte[] bufOut;
    byte[] bufIn;
    InetAddress address = null;
    DatagramPacket packetToSend = null;
    DatagramPacket packetIn = null;
    private String dir_ip="192.168.1.39";
    DatagramSocket dataSocket;
    String enviar;
    private PeticionListener listener;
    String accion;

    public HistoryTask(PeticionListener listener,String enviar){
        this.enviar=enviar;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        String recibido="0&";
        String argumentos[] = enviar.split("&");
        try {
            dataSocket = new DatagramSocket();
            address = InetAddress.getByName(dir_ip);
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            bufIn = new byte[4096];
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            byte[] last = "finish".getBytes();
            packetToSend = new DatagramPacket(last, last.length, address, 5555);
            dataSocket.send(packetToSend);
            packetIn = new DatagramPacket(bufIn, bufIn.length);
            dataSocket.receive(packetIn);
            recibido = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
            System.out.println("He recibido"+recibido);
            if(!recibido.equals("finish")){
                accion=recibido;
            }else{
                accion="0&";
            }

            while(!recibido.equals("finish")){
                dataSocket.receive(packetIn);
                recibido = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
                if (!recibido.equals("finish")){
                    accion+=recibido;
                }
            }
            accion=accion.trim();
        } catch (IOException ex) {
            Logger.getLogger(LoginRegisterAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();
        return recibido;
    }

    @Override
    public void onPostExecute(String resultado){
        super.onPostExecute(resultado);
        listener.callback(accion);
    }
}
