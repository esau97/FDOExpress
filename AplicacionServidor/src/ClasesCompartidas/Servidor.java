package ClasesCompartidas;

import Sesion.SesionEscritorio;
import Sesion.Sesiones;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
* Cuando ejecutamos el servidor, este lanza un hilo Sesiones que es el encargado de gestinar
* todas las peticiones UDP
* Además, el servidor es el encargado de gestinar las nuevas peticiones TCP,
* lanzando un hilo para cada nueva sesión (En este caso seria con la app escritorio)
*/
public class Servidor {
    private InformacionCompartida informacionCompartida;

    public static void main(String[] args) {
        Servidor serv = new Servidor();
        serv.informacionCompartida = new InformacionCompartida();
        try {
            ServerSocket serverSocket = null;
            boolean listening = true;
            serverSocket = new ServerSocket(4444);


            new Sesiones().start(); // Este hilo se encarga de gestionar todas
            // las peticiones UDP
            //new ComprobarRegistros().start();
            while (listening){
                Socket cliente = serverSocket.accept();
                new SesionEscritorio(cliente,serv.informacionCompartida).start();
            }
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
