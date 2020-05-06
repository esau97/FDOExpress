package Controllers;

import Util.Constantes;
import Util.Preferencias;
import javafx.scene.web.WebView;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseController {
    private Socket socket;
    private Preferencias pref;
    private BufferedReader in;
    private PrintWriter out;
    private DatagramSocket dataSocket;
    private InetAddress address = null;
    private DatagramPacket packetToSend = null;
    private DatagramPacket packetIn = null;

    private byte[] bufOut;
    private byte[] bufIn;

    public Preferencias getPref() {
        return pref;
    }

    public DatabaseController(Preferencias preferencias){
        pref = preferencias;
        try {
            address = InetAddress.getByName(pref.getDir_ip());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String enviarDatos(String email,String contrasena, int accion){
        String recibido="";
        String enviar="";
        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(contrasena)));
        enviar=accion+"&"+email+"&"+passwordCodif;

        try {
            dataSocket = new DatagramSocket();
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            System.out.println("Envio "+enviar);
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);

            byte[] last = "finish".getBytes();
            packetToSend = new DatagramPacket(last, last.length, address, 5555);
            dataSocket.send(packetToSend);
            bufIn = new byte[4096];
            packetIn = new DatagramPacket(bufIn, bufIn.length);

            dataSocket.receive(packetIn);
            String cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
            recibido+=cadena;
            while(!cadena.equals("finish")){
                dataSocket.receive(packetIn);
                cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
                if (!cadena.equals("finish")){
                    recibido+=cadena;
                }
            }
            recibido=recibido.trim();

        } catch (IOException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();
        return recibido;
    }
    public String enviarDatos(String fullName,String email,String password, String fullAddress,String phoneNumber,Integer rol){
        String recibido="";
        String enviar="";
        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));

        enviar=1+"&"+fullName+"&"+email+"&"+passwordCodif+"&"+fullAddress+"&"+phoneNumber+"&"+rol;

        try {
            dataSocket = new DatagramSocket();
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            byte[] last = "finish".getBytes();
            packetToSend = new DatagramPacket(last, last.length, address, 5555);
            dataSocket.send(packetToSend);
            bufIn = new byte[4096];
            packetIn = new DatagramPacket(bufIn, bufIn.length);
            dataSocket.receive(packetIn);
            String cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
            recibido+=cadena;
            while(!cadena.equals("finish")){
                dataSocket.receive(packetIn);
                cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
                if (!cadena.equals("finish")){
                    recibido+=cadena;
                }
            }
            recibido=recibido.trim();

        } catch (IOException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();
        return recibido;
    }

    public String enviarDatosVehiculo(String matricula, String purchaseDate, String revisionDate, File file,Integer idAdmin){

        String mensaje=2+"&"+matricula+"&"+purchaseDate+"&"+revisionDate+"&"+file.getName()+"&"+idAdmin;
        try {
            Socket socket = new Socket(pref.getDir_ip(),pref.getPuerto());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            String recibido = in.readLine();
            System.out.println(recibido);

            String argumentosRecibidos[] = recibido.split("&");

            if(!argumentosRecibidos[0].equals("0")){
                enviarFile(file);
            }
            System.out.println("Después de enviar los datos del fichero");
            in.close();
            out.close();
            return recibido;
            //stage.close();
        } catch (Exception e) {
            return "0&4";
        }

    }
    public void enviarFile(File file){
        // Get the size of the file
        new Thread(){
            @Override
            public void run(){
                long length = file.length();
                byte[] bytes = new byte[16 * 1024];
                InputStream in = null;
                OutputStream out;
                try {
                    in = new FileInputStream(file);
                     out= socket.getOutputStream();
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public String enviarDatosProveedor(String companyName, String email, String fullAddress, String phoneNumber) {
        String recibido="";
        String enviar = 6 +"&"+companyName+"&"+fullAddress+"&"+phoneNumber+"&"+email;
        try {
            dataSocket = new DatagramSocket();
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            bufIn = new byte[4096];
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            packetIn = new DatagramPacket(bufIn, bufIn.length);
            dataSocket.receive(packetIn);

            String cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
            recibido+=cadena;
            while(!cadena.equals("finish")){
                dataSocket.receive(packetIn);
                cadena = new String(packetIn.getData(), 0, packetIn.getLength()).trim();
                if (!cadena.equals("finish")){
                    recibido+=cadena;
                }
            }
            recibido=recibido.trim();

        } catch (IOException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();
        return recibido;
    }
    public JSONObject obtenerUbicacion(){
        JSONObject jsonObject = new JSONObject();
        try{
            out.println("7");
            String recibido = in.readLine();
            String argumentos [] = recibido.split("&");

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(argumentos[1]);
            System.out.println(jsonObject);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
