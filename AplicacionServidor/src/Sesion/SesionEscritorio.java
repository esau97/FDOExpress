package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.InformacionCompartida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        do{
            String respuesta="",mensaje="";
            try{
                cliente=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                print = new PrintWriter (socket.getOutputStream(), true);
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
                // Si el usuario se ha loqueado correctamente lo almaceno en una lista
                if (rsp[0].equals("1")){
                    informacionCompartida.setListaLogueados(argumentos[1]);
                }
                break;
            case REGISTRO:
                respuesta=bbdd.registro(argumentos);
                break;
            case REGISTRO_VEHICULO:
                respuesta = bbdd.registrarVehiculo(argumentos);
                break;
        }
        return respuesta;
    }

}
