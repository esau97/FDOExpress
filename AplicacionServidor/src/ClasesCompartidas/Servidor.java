package ClasesCompartidas;

import BaseDatos.BaseDeDatos;
import Sesion.PeticionesTCP;
import Sesion.PeticionesUDP;

import java.io.IOException;
import java.net.ServerSocket;

public class Servidor {
    private InformacionCompartida informacionCompartida;

    public static void main(String[] args) {
        Servidor serv = new Servidor();

        serv.informacionCompartida = new InformacionCompartida();
        try {
            ServerSocket serverSocket = null;
            boolean listening = true;
            serverSocket = new ServerSocket(6666);
            new Thread(){
                @Override
                public void run(){
                    BaseDeDatos bd = new BaseDeDatos();
                    bd.cargarDatosTablas();
                    bd.obtenerUbicacion();
                }

            }.start();

            new PeticionesUDP(serv.informacionCompartida).start(); // Este hilo se encarga de gestionar todas
            new PeticionesTCP().start();
            // las peticiones UDP

            /*while (listening){
                Socket cliente = serverSocket.accept();
                new SesionEscritorio(cliente,serv.informacionCompartida).start();
                System.out.println("Lanzando hilo sesion Escritorio");
            }*/

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
