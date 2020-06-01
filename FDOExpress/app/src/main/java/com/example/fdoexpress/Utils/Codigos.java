package com.example.fdoexpress.Utils;

public class Codigos {

    public enum CodigoRecibeCliente{
        ERROR,
        LOGIN_CORRECTO,
        CERRAR_SESION,
        REGISTRADO, // 3
        NUEVA_UBICACION,
        PEDIDOS_ACTIVOS,
        PEDIDO_ANADIDO, // 6
        HISTORIAL_PEDIDOS,
        VEHICULO_ASIGNADO

    }
    public static CodigoRecibeCliente codigo_cliente(int num){
        CodigoRecibeCliente[] cod = CodigoRecibeCliente.values();
        return cod[num];
    }
}
