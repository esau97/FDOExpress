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
        new Thread(){
            @Override
            public void run(){
                BaseDeDatos bd = new BaseDeDatos();
                bd.cargarDatosTablas();
                bd.obtenerUbicacion();
            }

        }.start();
        // Este hilo se encarga de gestionar todas las peticiones UDP
        new PeticionesUDP(serv.informacionCompartida).start();
        // Este hilo se encarga de gestionar todas las peticiones TCP
        new PeticionesTCP().start();


    }

}
