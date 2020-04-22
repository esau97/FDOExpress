package ClasesCompartidas;

import Sesion.SesionEscritorio;
import Sesion.PeticionesUDP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private InformacionCompartida informacionCompartida;

    public static void main(String[] args) {
        Servidor serv = new Servidor();
        serv.informacionCompartida = new InformacionCompartida();
        try {
            ServerSocket serverSocket = null;
            boolean listening = true;
            serverSocket = new ServerSocket(4444);


            new PeticionesUDP(serv.informacionCompartida).start(); // Este hilo se encarga de gestionar todas
            // las peticiones UDP
            //new ComprobarRegistros().start();
            while (listening){
                Socket cliente = serverSocket.accept();
                new SesionEscritorio(cliente,serv.informacionCompartida).start();
                System.out.println("Lanzando hilo sesion Escritorio");
            }
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
