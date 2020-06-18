package Sesion;

import BaseDatos.BaseDeDatos;
import ClasesCompartidas.Codigos;
import ClasesCompartidas.Constantes;
import ClasesCompartidas.InformacionCompartida;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
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
    private String mensajeRecibido;

    public PeticionUDP(DatagramPacket packetIn, DatagramSocket dataSocket, InformacionCompartida informacionCompartida,String mensajeRecibido) {
        this.informacionCompartida = informacionCompartida;
        this.recibido = mensajeRecibido;
        bbdd=new BaseDeDatos();
        this.dataSocket = dataSocket;
        paquete_entrada=packetIn;
        //recibido = new String(paquete_entrada.getData(), 0, paquete_entrada.getLength());
        address = paquete_entrada.getAddress();
        port = paquete_entrada.getPort();
    }
    public void run(){

        String enviar;
        byte bufOut[] ;
        DatagramPacket packetOut;
        System.out.println("NUEVA PETICION UDP");
        try {
            System.out.println("Recibido"+recibido);
            enviar=tratarMensaje(recibido);
            bufOut=enviar.getBytes();
            packetOut = new DatagramPacket(bufOut,bufOut.length,address,port);
            dataSocket.send(packetOut);
            System.out.println("Enviando por UDP "+enviar);

        } catch (IOException ex) {
            Logger.getLogger(PeticionUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String tratarMensaje(String codigo){
        String respuesta="";

        String argumentos[]=codigo.split("&");
        int num=Integer.parseInt(argumentos[0]);
        switch(Codigos.codigo_servidor(num)){
            case REGISTRO_TRABAJADOR:
                System.out.println("REGISTRO");
                respuesta=bbdd.registroTrabajador(argumentos);
                System.out.println("Envio"+respuesta);
                break;

            case OBTENER_UBICACION:
                JSONParser parser = new JSONParser();
                try {
                    JSONObject root = (JSONObject) parser.parse(new FileReader(Constantes.rutaFicheros+"/ubicaciones.json"));
                    respuesta+="8&"+root.toJSONString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case NUEVOS_PEDIDOS:
                respuesta = bbdd.altaNuevosPedidos(argumentos);
                break;

            case OBTENER_UBICACION_PEDIDO:
                respuesta = "4&"+ bbdd.ubicacionPedido(argumentos[1]);
                break;
            case CAMBIAR_ESTADO_PEDIDO:
                // Recibo por argumentos una descripcion dada por el repartidor, el nuevo estado
                // y el codigo del pedido
                respuesta = bbdd.cambiarEstadoPedido(argumentos);
                break;

            case ASIGNAR_PEDIDOS_RECOGER:
                respuesta = bbdd.asignarRutas();
                break;
            case MODIFICAR_RUTA:
                respuesta = bbdd.cambiarRuta(argumentos[1],argumentos[2]);
                break;
        }

        return respuesta;
    }


}
