package Controllers;

import Util.Constantes;
import Util.Preferencias;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DatabaseController {
    private Socket socket;
    private Preferencias pref;


    public DatabaseController(){
        pref = new Preferencias();
        socket = pref.getSocket();
    }

    public String enviarDatos(String email,String contrasena, int accion){

        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(contrasena)));
        String mensaje=accion+"&"+email+"&"+passwordCodif;
        Constantes.DATOS_USUARIO.setEmail(email);
        System.out.println("enviando datos...");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            String recibido = in.readLine();
            System.out.println(recibido);
            return recibido;

        } catch (IOException e) {
            return "0&4";
        }
    }
    public String enviarDatos(String fullName,String email,String password, String fullAddress,String phoneNumber,Integer rol){

        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
        String mensaje=1+"&"+fullName+"&"+email+"&"+passwordCodif+"&"+fullAddress+"&"+phoneNumber+"&"+rol;

        System.out.println("enviando datos...");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            String recibido = in.readLine();
            System.out.println(recibido);
            //tratarMensaje(recibido);

            in.close();
            out.close();
            return recibido;
            //stage.close();
        } catch (Exception e) {
            return "0&4";
        }

    }
    public String enviarDatosVehiculo(){
        String respuesta= "";
        return respuesta;
    }
}
