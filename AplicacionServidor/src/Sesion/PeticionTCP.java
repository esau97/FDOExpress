package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class PeticionTCP extends Thread{
    private Socket cliente;
    private BufferedReader in ;
    private PrintWriter out ;
    private BaseDeDatos bbdd;
    public PeticionTCP(Socket cliente){
        this.cliente=cliente;
        this.bbdd = new BaseDeDatos();
        try{
            in=new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            out = new PrintWriter (cliente.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run(){
        String respuesta="",mensaje="";
        try{
            respuesta=in.readLine();
            System.out.println("Recibo"+respuesta);
            // Tratamos el mensaje recibido
            mensaje=tratarMensaje(respuesta);
            System.out.println("Envio"+mensaje);
            //Enviamos una respuesta al cliente

            out.println(mensaje);
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String tratarMensaje(String codigo){
        String respuesta="";
        String argumentos[] = codigo.split("&");
        switch (Codigos.codigo_servidor(Integer.parseInt(argumentos[0]))){

            case REGISTRO_VEHICULO:
                respuesta = bbdd.registrarVehiculo(argumentos);
                out.println(respuesta);
                guardarArchivo(argumentos[1],argumentos[5]);
                break;
            case DESCARGAR_ARCHIVO:
                respuesta = enviarFile(new File("Ficheros/"+argumentos[1]+"_"+argumentos[2]));
                break;
            case OBTENER_UBICACION:
                JSONParser parser = new JSONParser();
                try {
                    JSONObject root = (JSONObject) parser.parse(new FileReader("Ficheros/ubicaciones.json"));
                    respuesta+="8&"+root.toJSONString();
                    boolean connected = true;
                    out.println(respuesta);
                    while (connected){

                        System.out.println("bucle");
                        bbdd.obtenerUbicacion();
                        root = (JSONObject) parser.parse(new FileReader("Ficheros/ubicaciones.json"));
                        respuesta="8&"+root.toJSONString();
                        System.out.println("Envio respuesta "+respuesta);

                        out.println(respuesta);
                        if(in.readLine().equals("0")){
                            connected=false;
                        }
                    }
                    System.out.println(respuesta);
                } catch (SocketException e){
                    Thread.interrupted();
                    System.out.println("Socket cerrado");
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
        return respuesta;
    }
    public void guardarArchivo(String matricula,String nombreArchivo){
        InputStream in = null;
        OutputStream out = null;
        try {
            in = cliente.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }
        try {
            out = new FileOutputStream("Fichero/"+matricula+"_"+nombreArchivo);
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
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String enviarFile(File file){
        String respuesta="";
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            OutputStream out = cliente.getOutputStream();
            /*RandomAccessFile raf=new RandomAccessFile(file,"rw");
            byte count=0;
            long posicion=raf.getFilePointer();

            while(posicion<file.length()){
                count=raf.readByte();
                out.write(count);
                posicion=raf.getFilePointer();
            }
            raf.close();*/
            int count;
            while ((count = in.read(bytes)) > 0) {
                System.out.println(count);
                out.write(bytes, 0, count);
            }
            out.close();
        } catch (FileNotFoundException e) {
            respuesta="0&2";
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        respuesta="5";
        return respuesta;
    }
}
