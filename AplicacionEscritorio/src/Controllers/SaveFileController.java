package Controllers;

import Util.Preferencias;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SaveFileController extends Thread{
    private Preferencias preferencias;
    private Socket socket;
    private MyCallback myCallback;
    private String ruta,matricula,nombreArchivo;
    public SaveFileController(DatabaseController databaseController,String ruta,String matricula,String nombreArchivo){
        preferencias = databaseController.getPref();
        this.ruta=ruta;
        this.matricula=matricula;
        this.nombreArchivo=nombreArchivo;
        try{
            this.socket=new Socket(preferencias.getDir_ip(),preferencias.getPuerto());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        InputStream in = null;
        OutputStream out = null;

            System.out.println("Guardando archivo");
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
                while ((count = in.read(bytes)) != -1) {
                    System.out.println(count);
                    out.write(bytes, 0, count);
                }
                myCallback.callback("5&");
                System.out.println("Despues de llamar al callback");
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

}
