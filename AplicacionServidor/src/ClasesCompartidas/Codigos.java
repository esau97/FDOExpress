package ClasesCompartidas;

public class Codigos {

    public enum recibeServidor{
        INICIO_SESION,
        REGISTRO
    }

    public static recibeServidor codigo_servidor(int num){
        recibeServidor[] cod = recibeServidor.values();
        return cod[num];
    }
}
