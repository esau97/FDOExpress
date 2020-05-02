package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.InformacionCompartida;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SesionEscritorio extends Thread{
    private Socket socket;
    private InformacionCompartida informacionCompartida;
    private BaseDeDatos bbdd;
    private boolean listening=false;
    private BufferedReader cliente ;
    private PrintWriter print ;
    public SesionEscritorio(Socket socket, InformacionCompartida informacionCompartida) {
        this.socket=socket;
        this.informacionCompartida=informacionCompartida;
    }
    public void run(){

        System.out.println("Empieza la sesion...");
        listening = true;
        bbdd = new BaseDeDatos();
        try {
            cliente=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            print = new PrintWriter (socket.getOutputStream(), true);


        } catch (IOException e) {
            e.printStackTrace();
        }
        do{
            String respuesta="",mensaje="";
            try{

                while(listening){

                    if(cliente.ready()){
                        // Leemos lo que nos envía el cliente
                        respuesta=cliente.readLine();
                        System.out.println("Recibo"+respuesta);
                        // Tratamos el mensaje recibido
                        mensaje=tratarMensaje(respuesta);
                        System.out.println("Envio"+mensaje);
                        //Enviamos una respuesta al cliente
                        print.println(mensaje);
                    }
                    Thread.sleep(2000);
                }

                socket.close();
                System.out.println("Cerrando sesión..");

            } catch (IOException ex) {
                Logger.getLogger(SesionEscritorio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(SesionEscritorio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(listening);
    }

    public String tratarMensaje(String codigo){
        String respuesta="";
        String argumentos[] = codigo.split("&");
        switch (Codigos.codigo_servidor(Integer.parseInt(argumentos[0]))){
            case INICIO_SESION:
                respuesta=bbdd.iniciarSesion(argumentos[1],argumentos[2]);

                String rsp [] = respuesta.split("&");
                System.out.println(rsp[0]);
                // Si el usuario se ha loqueado correctamente lo almaceno en una lista
                if (rsp[0].equals("1")){
                    informacionCompartida.setListaLogueados(argumentos[1]);
                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject root = (JSONObject) parser.parse(new FileReader("Ficheros/tablas.json"));
                        respuesta+="&"+root.toJSONString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REGISTRO:
                respuesta=bbdd.registro(argumentos);
                break;
            case REGISTRO_VEHICULO:
                respuesta = bbdd.registrarVehiculo(argumentos);
                print.println(respuesta);
                guardarArchivo(argumentos[1],argumentos[5]);
            case DESCARGAR_ARCHIVO:
                respuesta = enviarFile(new File("Ficheros/"+argumentos[1]+"_"+argumentos[2]));
                break;
            case REGISTRO_PROVEEDOR:
                respuesta=bbdd.registrarProveedor(argumentos);
                break;
        }
        return respuesta;
    }
    public void guardarArchivo(String matricula,String nombreArchivo){
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
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
            OutputStream out = socket.getOutputStream();
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
