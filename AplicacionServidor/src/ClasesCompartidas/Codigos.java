package ClasesCompartidas;

public class Codigos {
    public enum recibeCliente{
        INICIAR_SESION,
        REGISTRARSE


    }

    public enum recibeServidor{
        INICION_SESION
    }

    public static recibeCliente codigo_cliente(int num){
        recibeCliente[] cod = recibeCliente.values();
        return cod[num];
    }

    public static recibeServidor codigo_servidor(int num){
        recibeServidor[] cod = recibeServidor.values();
        return cod[num];
    }
}
