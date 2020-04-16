package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.InformacionCompartida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SesionMovil extends Thread{
    private BaseDeDatos bbdd;

    private DatagramPacket paquete_entrada;
    private DatagramSocket dataSocket;
    private String recibido;
    private int port;
    private InetAddress address;

    public SesionMovil(DatagramPacket packetIn, DatagramSocket dataSocket) {
        bbdd=new BaseDeDatos();
        this.dataSocket = dataSocket;
        paquete_entrada=packetIn;
        System.out.println("Tamaño "+paquete_entrada.getLength());
        System.out.println("Tamaño in"+packetIn.getLength());
        recibido = new String(paquete_entrada.getData(), 0, paquete_entrada.getLength());
        address = paquete_entrada.getAddress();
        port = paquete_entrada.getPort();
    }
    public void run(){
        System.out.println("Comienza el registro.");

        String enviar;

        byte bufOut[] ;

        DatagramPacket packetOut;

        try {
            System.out.println("Recibido"+recibido);
            enviar=tratarMensaje(recibido);
            bufOut=enviar.getBytes();
            packetOut = new DatagramPacket(bufOut, bufOut.length, address, port);
            dataSocket.send(packetOut);

        } catch (IOException ex) {
            Logger.getLogger(SesionMovil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String tratarMensaje(String codigo){
        String respuesta="";

        System.out.println(codigo);
        String argumentos[]=codigo.split("&");
        int num=Integer.parseInt(argumentos[0]);
        switch(Codigos.codigo_servidor(num)){
            case REGISTRO:
                System.out.println("REGISTRO");
                respuesta=bbdd.registro(argumentos);
                System.out.println("Envio"+respuesta);
                break;
            case INICIO_SESION:
                respuesta=bbdd.iniciarSesion(argumentos[1],argumentos[2]);
                break;
        }

        return respuesta;
    }
}