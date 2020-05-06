package ClasesCompartidas;

public class Codigos {

    public enum recibeServidor{
        INICIO_SESION,
        REGISTRO,
        REGISTRO_VEHICULO,
        CARGAR_TABLAS, //3
        GUARDAR_ARCHIVO,
        DESCARGAR_ARCHIVO,
        REGISTRO_PROVEEDOR, //6
        OBTENER_UBICACION,
        NUEVOS_PEDIDOS
    }

    public static recibeServidor codigo_servidor(int num){
        recibeServidor[] cod = recibeServidor.values();
        return cod[num];
    }
}
