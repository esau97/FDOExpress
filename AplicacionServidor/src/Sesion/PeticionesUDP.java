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
        bufIn = new byte[256]; //A buffer to get whatever information the client send with the request
        packetIn = new DatagramPacket(bufIn, bufIn.length);
        while(listening){
            try {
                dataSocket.receive(packetIn);
                System.out.println("Recibido");
                new SesionMovil(packetIn,dataSocket,informacionCompartida).start();

            } catch (IOException ex) {
                Logger.getLogger(PeticionesUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
