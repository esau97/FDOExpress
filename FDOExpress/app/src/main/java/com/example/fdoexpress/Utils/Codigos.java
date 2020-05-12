package com.example.fdoexpress.Utils;

public class Codigos {

    public enum CodigoRecibeCliente{
        ERROR,
        LOGIN_CORRECTO,
        CERRAR_SESION,
        REGISTRADO,
        NUEVA_UBICACION,
        PEDIDOS_ACTIVOS


    }
    public static CodigoRecibeCliente codigo_cliente(int num){
        CodigoRecibeCliente[] cod = CodigoRecibeCliente.values();
        return cod[num];
    }
}
