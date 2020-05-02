package Controllers;

import Util.Constantes;
import Util.Preferencias;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.Socket;

public class DatabaseController {
    private Socket socket;
    private Preferencias pref;
    private MyCallback myCallback;


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
    public String enviarDatosVehiculo(String matricula, String purchaseDate, String revisionDate, File file,Integer idAdmin){

        String mensaje=2+"&"+matricula+"&"+purchaseDate+"&"+revisionDate+"&"+file.getName()+"&"+idAdmin;
        try {
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
    public void guardarArchivo(String ruta,String matricula,String nombreArchivo){

        new Thread(){
            InputStream in = null;
            OutputStream out = null;
            MyCallback myCallback;
            @Override
            public void run(){
                System.out.println("guardando archivo");
                String respuesta="";
                PrintWriter pw = null;

                try {
                    pw = new PrintWriter(socket.getOutputStream(), true);
                    pw.println("5&"+matricula+"&"+nombreArchivo);
                    in = socket.getInputStream();
                } catch (IOException ex) {
                    System.out.println("Can't get socket input stream. ");
                }
                try {
                    out = new FileOutputStream(ruta+"/"+matricula+"_"+nombreArchivo);
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found. ");
                }
                byte[] bytes = new byte[16*1024];
                System.out.println("Recibiendo datos");
                int count;
                try{
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    myCallback.callback("5&");
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    myCallback.callback("0&1");
                }
                pw.close();
            }
            public void setCallbackPerfomed(MyCallback myCallback){
                this.myCallback=myCallback;
            }
        }.start();

    }
    public String enviarDatosProveedor(String companyName, String email, String fullAddress, String phoneNumber) {
        String mensaje = 6 +"&"+companyName+"&"+fullAddress+"&"+phoneNumber+"&"+email;
       try{
           BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
           out.println(mensaje);
           String recibido = in.readLine();
           in.close();
           out.close();
           return recibido;
       } catch (IOException e) {
           e.printStackTrace();
           return "0&4";
       }
    }
}
