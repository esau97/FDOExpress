package Sesion;

import ClasesCompartidas.InformacionCompartida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeticionesUDP extends Thread {
    private DatagramPacket paquete_entrada;
    private DatagramSocket dataSocket;
    private InformacionCompartida informacionCompartida;
    private boolean listening=true;

    public PeticionesUDP(InformacionCompartida informacionCompartida){
        try {
            this.informacionCompartida=informacionCompartida;
            dataSocket = new DatagramSocket(5555);
        } catch (SocketException ex) {
            Logger.getLogger(PeticionesUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run(){
        System.out.println("Leyendo..");
        byte[] bufIn;
        DatagramPacket packetIn;
        bufIn = new byte[4096]; //A buffer to get whatever information the client send with the request
        packetIn = new DatagramPacket(bufIn, bufIn.length);
        String recibido;
        String enviar="";
        while(listening){
            try {
                dataSocket.receive(packetIn);
                recibido="";
                String cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
                recibido+=cadena;
                System.out.println(recibido);

                enviar=recibido.trim();
                new PeticionUDP(packetIn,dataSocket,informacionCompartida,enviar).start();

            } catch (IOException ex) {
                Logger.getLogger(PeticionesUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
