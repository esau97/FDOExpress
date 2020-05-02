package Util;

public class Codigos {
    public enum CodigoRecibeCliente{
        ERROR,
        LOGIN_CORRECTO,
        CERRAR_SESION,
        REGISTRO, //3
        DATOS_TABLAS,
        ARCHIVO_GUARDADO,
        REGISTRO_VEHICULO,
        REGISTRO_PROVEEDOR
    }
    public static CodigoRecibeCliente codigo_cliente(int num){
        CodigoRecibeCliente[] cod = CodigoRecibeCliente.values();
        return cod[num];
    }
}
