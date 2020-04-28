package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.InformacionCompartida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeticionUDP extends Thread{
    private BaseDeDatos bbdd;

    private DatagramPacket paquete_entrada;
    private DatagramSocket dataSocket;
    private String recibido;
    private int port;
    private InetAddress address;
    private InformacionCompartida informacionCompartida;

    public PeticionUDP(DatagramPacket packetIn, DatagramSocket dataSocket, InformacionCompartida informacionCompartida) {
        this.informacionCompartida = informacionCompartida;
        bbdd=new BaseDeDatos();
        this.dataSocket = dataSocket;
        paquete_entrada=packetIn;
        recibido = new String(paquete_entrada.getData(), 0, paquete_entrada.getLength());
        address = paquete_entrada.getAddress();
        port = paquete_entrada.getPort();
    }
    public void run(){

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
            Logger.getLogger(PeticionUDP.class.getName()).log(Level.SEVERE, null, ex);
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
                System.out.println("INICIO SESION");
                respuesta=bbdd.iniciarSesion(argumentos[1],argumentos[2]);
                String rsp [] = respuesta.split("&");
                // Si el usuario se ha loqueado correctamente lo almaceno en una lista
                if (rsp[0].equals("1")){
                    informacionCompartida.setListaLogueados(argumentos[1]);
                }
                break;
            case CARGAR_TABLAS:
                System.out.println("CARGANDO TABLAS");
                respuesta = bbdd.devolverDatosEmpleados();
                break;
        }

        return respuesta;
    }
}
