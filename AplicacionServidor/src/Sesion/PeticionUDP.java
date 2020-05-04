package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.InformacionCompartida;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeticionUDP extends Thread{
    private BaseDeDatos bbdd;
    private DatagramPacket paquete_entrada;
    private DatagramSocket dataSocket;
    private String recibido;
    private int port;
    private InetAddress address;
    private InformacionCompartida informacionCompartida;

    public PeticionUDP(DatagramPacket packetIn, DatagramSocket dataSocket, InformacionCompartida informacionCompartida) {
        this.informacionCompartida = informacionCompartida;
        bbdd=new BaseDeDatos();
        this.dataSocket = dataSocket;
        paquete_entrada=packetIn;
        recibido = new String(paquete_entrada.getData(), 0, paquete_entrada.getLength());
        address = paquete_entrada.getAddress();
        port = paquete_entrada.getPort();
    }
    public void run(){

        String enviar;
        byte bufOut[] ;
        DatagramPacket packetOut;

        try {
            System.out.println("Recibido"+recibido);
            enviar=tratarMensaje(recibido);
            bufOut=enviar.getBytes();
            packetOut = new DatagramPacket(bufOut, bufOut.length, address, port);
            int maxValue=0;
            while(maxValue<=bufOut.length){
                byte[] slice = Arrays.copyOfRange(bufOut, maxValue, maxValue+4096);
                maxValue+=4096;
                packetOut = new DatagramPacket(slice, slice.length, address, port);
                dataSocket.send(packetOut);
                System.out.println("Enviando"+new String(packetOut.getData(), 0, packetOut.getLength()));
            }
            byte[] last = "finish".getBytes();
            packetOut = new DatagramPacket(last, last.length, address, port);
            dataSocket.send(packetOut);

        } catch (IOException ex) {
            Logger.getLogger(PeticionUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String tratarMensaje(String codigo){
        String respuesta="";

        String argumentos[]=codigo.split("&");
        int num=Integer.parseInt(argumentos[0]);
        switch(Codigos.codigo_servidor(num)){
            case REGISTRO:
                System.out.println("REGISTRO");
                respuesta=bbdd.registro(argumentos);
                System.out.println("Envio"+respuesta);
                break;
            case INICIO_SESION:
                System.out.println("INICIO SESION");
                respuesta=bbdd.iniciarSesion(argumentos[1],argumentos[2]);
                System.out.println(respuesta);
                String rsp [] = respuesta.split("&");
                // Si el usuario se ha loqueado correctamente lo almaceno en una lista
                if (rsp[0].equals("1") && (rsp[1].equals("1")||rsp[1].equals("4"))){
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
            case CARGAR_TABLAS:
                System.out.println("CARGANDO TABLAS");
                respuesta = bbdd.devolverDatosEmpleados();
                break;

            case REGISTRO_PROVEEDOR:
                respuesta=bbdd.registrarProveedor(argumentos);
                break;
            case OBTENER_UBICACION:
                JSONParser parser = new JSONParser();
                try {
                    JSONObject root = (JSONObject) parser.parse(new FileReader("Ficheros/ubicaciones.json"));
                    respuesta+="8&"+root.toJSONString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return respuesta;
    }


}
