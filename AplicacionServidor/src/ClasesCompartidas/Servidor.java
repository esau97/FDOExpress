package ClasesCompartidas;

import Sesion.SesionEscritorio;

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

            //new ComprobarRegistros().start();
            while (listening){
                Socket cliente = serverSocket.accept();
                new SesionEscritorio(cliente,serv.informacionCompartida).start();

            }
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Error de conexion con el Servidor");
        }
    }

}