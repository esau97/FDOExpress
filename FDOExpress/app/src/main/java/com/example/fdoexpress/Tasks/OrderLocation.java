package com.example.fdoexpress.Tasks;

import android.util.Log;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.Utils.Constantes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderLocation extends Thread{
    private PeticionListener peticionListener;
    private String codigoPedido;
    private boolean connected;
    private String dir_ip= Constantes.dir_ip;
    byte[] bufOut;
    byte[] bufIn;
    InetAddress address = null;
    DatagramPacket packetToSend = null;
    DatagramPacket packetIn = null;
    DatagramSocket dataSocket;
    private String enviar="";

    public OrderLocation(PeticionListener peticionListener,String codigoPedido) {
        this.peticionListener = peticionListener;
        this.codigoPedido = codigoPedido;
    }
    @Override
    public void run(){
        try{
            Log.i("OrderLocation","Location");

            connected=true;
            String recibido="",cadena="";
            enviar="11&"+codigoPedido;
            bufIn = new byte[4096];
            dataSocket = new DatagramSocket();
            address = InetAddress.getByName(dir_ip);
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            dataSocket.setBroadcast(true);
            packetIn = new DatagramPacket(bufIn, bufIn.length);
            while (connected){
                if(Thread.interrupted()){
                    connected=false;
                }
                System.out.println("En bucle");
                packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
                dataSocket.send(packetToSend);
                dataSocket.receive(packetIn);
                recibido = new String(packetIn.getData(), 0, packetIn.getLength()).trim();

                recibido=recibido.trim();
                System.out.println(recibido);
                peticionListener.callback(recibido);
                recibido="";
                Thread.sleep(20000);
            }

        }catch (InterruptedException e) {
            connected=false;
            Log.i("Interrumpido","Hilo order interrumpido");
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(MainAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
