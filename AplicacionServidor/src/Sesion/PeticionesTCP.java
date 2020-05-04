package Sesion;

import ClasesCompartidas.Codigos;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PeticionesTCP extends Thread{
    boolean listening=true;
    private ServerSocket serverSocket;
    public PeticionesTCP(){
        this.serverSocket = serverSocket;
    }
    @Override
    public void run(){
        try{
            System.out.println("Lanzando hilo peticiones tcp");
            serverSocket = new ServerSocket(4444);
            while (listening){
                Socket cliente = serverSocket.accept();
                new PeticionTCP(cliente).start();
                System.out.println("Lanzando hilo sesion Escritorio");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
