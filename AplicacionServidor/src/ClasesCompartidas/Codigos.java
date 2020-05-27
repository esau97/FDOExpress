package ClasesCompartidas;

public class Codigos {

    public enum recibeServidor{
        INICIO_SESION,
        REGISTRO,
        REGISTRO_VEHICULO,
        CARGAR_TABLAS,                  //3
        GUARDAR_ARCHIVO,
        DESCARGAR_ARCHIVO,
        REGISTRO_PROVEEDOR,             //6
        OBTENER_UBICACION,
        NUEVOS_PEDIDOS,
        OBTENER_PEDIDOS_ACTIVOS,        //9
        OBTENER_HISTORIAL_PEDIDOS,
        OBTENER_UBICACION_PEDIDO,
        PETICION_UBICACION_TERMINADA,   //12
        CAMBIAR_ESTADO_PEDIDO,
        ASIGNAR_PEDIDOS_RECOGER,
        MODIFICAR_RUTA,                 //15
        HISTORIAL_PEDIDOS
    }

    public static recibeServidor codigo_servidor(int num){
        recibeServidor[] cod = recibeServidor.values();
        return cod[num];
    }
}
