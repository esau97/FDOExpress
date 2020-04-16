package ClasesCompartidas;

import java.util.LinkedList;
import java.util.TreeMap;

public class InformacionCompartida {


    private LinkedList<String> listaLogueados;

    public InformacionCompartida() {
        listaLogueados = new LinkedList<>();
    }

    public LinkedList<String> getListaLogueados() {
        return listaLogueados;
    }

    public void setListaLogueados(String nombreUsuario) {
        this.listaLogueados.add(nombreUsuario);
    }
}
