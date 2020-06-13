package Controllers;

import Util.Preferencias;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseController {

    private Preferencias pref;
    private BufferedReader in;
    private PrintWriter out;
    private DatagramSocket dataSocket;
    private InetAddress address = null;
    private DatagramPacket packetToSend = null;
    private DatagramPacket packetIn = null;
    private MyCallback callback;

    private byte[] bufOut;
    private byte[] bufIn;

    public Preferencias getPref() {
        return pref;
    }

    public DatabaseController(Preferencias preferencias) {
        pref = preferencias;
        try {
            address = InetAddress.getByName(pref.getDir_ip());
            System.out.println("Guardando IP");
        } catch (UnknownHostException unknownHostException){
            System.out.println("Error al introducir la direccion IP");
        }catch (IOException e) {
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
            System.out.println("Recibido"+recibido);
            callback.callback(recibido);

        } catch (UnknownHostException unknownHostException){
            System.out.println("Error al introducir la IP");
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
                enviarFile(file,matricula,socket,in,out);
            }
            System.out.println("Después de enviar los datos del fichero");

            return recibido;
            //stage.close();
        } catch (Exception e) {
            return "0&4";
        }
    }

    public void enviarFile(File file,String matricula,Socket socket,BufferedReader br, PrintWriter pw){
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
                    pw.close();
                    br.close();
                    out.close();
                    socket.close();
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
            Socket socket = new Socket(pref.getDir_ip(),pref.getPuerto());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(enviar);
            recibido = in.readLine();
            System.out.println(recibido);

            System.out.println("Después de enviar los datos del fichero");

            //stage.close();
        } catch (Exception e) {
            return "0&4";
        }

        return recibido;
    }
    public JSONObject obtenerUbicacion(){
        JSONObject jsonObject = new JSONObject();
        try{
            Socket socket = new Socket(pref.getDir_ip(),4444);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("7");
            String recibido = in.readLine();
            String argumentos [] = recibido.split("&");

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(argumentos[1]);
            System.out.println(jsonObject);
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String actualizarRutas(){
        String recibido="";
        String enviar = 14+"&";
        try {
            dataSocket = new DatagramSocket();
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            bufIn = new byte[4096];
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            byte[] last = "finish".getBytes();
            packetToSend = new DatagramPacket(last, last.length, address, 5555);
            dataSocket.send(packetToSend);

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();
        return recibido;
    }

    public String actualizarTablas(){
        String respuesta="";
        String mensaje=3+"";
        try {
            Socket socket = new Socket(pref.getDir_ip(),pref.getPuerto());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            respuesta = in.readLine();
            System.out.println("Nuevas rutas"+respuesta);
            in.close();
            out.close();

            //stage.close();
        } catch (Exception e) {
            return "0&4";
        }
        return respuesta;
    }

    public String modificarRuta(String nRuta,String tfno){
        String recibido="";

        String enviar = 15 +"&"+ nRuta +"&"+ tfno;
        try {
            dataSocket = new DatagramSocket();
            bufOut = enviar.getBytes(); //In this program, no information is set by the client
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            bufIn = new byte[4096];
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            byte[] last = "finish".getBytes();
            packetToSend = new DatagramPacket(last, last.length, address, 5555);
            dataSocket.send(packetToSend);

        } catch (IOException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSocket.close();

        return recibido;
    }

    public void setCallbackPerformed(MyCallback callback){
        this.callback=callback;
    }
}
