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
    private OutputStream outputStream;
    private InputStream inputStream;

    public PeticionTCP(Socket cliente){
        this.cliente=cliente;
        this.bbdd = new BaseDeDatos();
        try{
            inputStream =cliente.getInputStream();
            in=new BufferedReader(new InputStreamReader(inputStream));
            outputStream = cliente.getOutputStream();
            out = new PrintWriter (outputStream, true);
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
            case INICIO_SESION:
                System.out.println("INICIO SESION");
                respuesta=bbdd.iniciarSesion(argumentos[1],argumentos[2]);
                System.out.println(respuesta);
                String rsp [] = respuesta.split("&");
                // Si el usuario se ha loqueado correctamente lo almaceno en una lista
                if (rsp[0].equals("1") && (rsp[1].equals("1")||rsp[1].equals("4"))){

                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject root = (JSONObject) parser.parse(new FileReader("Ficheros/tablas.json"));
                        respuesta+="&"+root.toJSONString();
                        // TODO: cambiar el codigo en el lado escritorio
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Compruebo si se ha logueado correctamente y si el usuario
                    // es un receptor, si es así obtengo sus pedidos.
                }else if(rsp[0].equals("1") && rsp[1].equals("3")){

                    String [] cod = respuesta.split("&");
                    respuesta+="&"+bbdd.devolverPedidosActivos(cod[4]);
                }else if(rsp[0].equals("1") && rsp[1].equals("2")){
                    String [] cod = respuesta.split("&");
                    respuesta+="&"+bbdd.pedidosReparto(cod[3]);
                }
                break;
            case REGISTRO_VEHICULO:
                respuesta = bbdd.registrarVehiculo(argumentos);
                out.println(respuesta);
                guardarArchivo(argumentos[1],argumentos[4]);
                break;
            case REGISTRO_PROVEEDOR:
                respuesta=bbdd.registrarProveedor(argumentos);
                break;
            case NUEVOS_PEDIDOS:
                respuesta = bbdd.altaNuevosPedidos(argumentos);
                break;
            case REGISTRO:
                respuesta = bbdd.registro(argumentos);
                break;
            case DESCARGAR_ARCHIVO:
                respuesta = enviarFile(new File("Ficheros/"+argumentos[1]+"_"+argumentos[2]));
                break;
            case GUARDAR_ARCHIVO:
                respuesta = guardarArchivo(argumentos[1],argumentos[2]);
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
                    System.out.println("Cerrando sesion");
                } catch (SocketException e){
                    Thread.interrupted();
                    System.out.println("Socket cerrado");
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case OBTENER_PEDIDOS_ACTIVOS:
                respuesta=bbdd.devolverPedidosActivos(argumentos[1]);
                break;

            case OBTENER_UBICACION_PEDIDO:
                respuesta = "4&"+ bbdd.ubicacionPedido(argumentos[1]);
                break;
            case HISTORIAL_PEDIDOS:
                respuesta = bbdd.historialPedidos(argumentos[1]);
                break;
            case CAMBIAR_ESTADO_PEDIDO:
                // Recibo por argumentos una descripcion dada por el repartidor, el nuevo estado
                // y el codigo del pedido
                respuesta = bbdd.cambiarEstadoPedido(argumentos);
                break;
            case PEDIDOS_REPARTIR:
                respuesta = "5&"+bbdd.pedidosReparto(argumentos[1]);
                //respuesta+="&"+ bbdd.pedidosRecoger(argumentos[1]);
                break;
            case ASIGNAR_VEHICULO:
                respuesta = ""+bbdd.asignarVehiculo(argumentos[1],argumentos[2]);
                break;
            case ACTUALIZAR_UBICACION:
                respuesta = bbdd.actualizarUbicacion(argumentos);
                break;
            case CARGAR_TABLAS:
                System.out.println("CARGANDO TABLAS");
                respuesta = "4&"+bbdd.devolverDatosTablas();
                break;
        }
        return respuesta;
    }
    public String guardarArchivo(String matricula,String nombreArchivo){
        String respuesta = "";
        InputStream in = null;
        OutputStream out = null;
        in = inputStream;

        try {
            out = new FileOutputStream("Ficheros/"+matricula+"_"+nombreArchivo);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[16*1024];
        System.out.println("Recibiendo datos");
        int count;
        try{
            System.out.println("Está cerrado?"+cliente.isClosed());
            System.out.println("Tamaño leer "+in.available());

            while ((count = in.read(bytes)) != -1) {
                System.out.println(count);
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
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
