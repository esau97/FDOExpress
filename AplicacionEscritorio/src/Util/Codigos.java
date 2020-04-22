package Util;

public class Codigos {
    public enum CodigoRecibeCliente{
        ERROR,
        LOGIN_CORRECTO,
        CERRAR_SESION,
        REGISTRO
    }
    public static CodigoRecibeCliente codigo_cliente(int num){
        CodigoRecibeCliente[] cod = CodigoRecibeCliente.values();
        return cod[num];
    }
}
