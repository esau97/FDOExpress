package Util;

import java.io.IOException;
import java.net.Socket;

public class Preferencias {
    private String dir_ip="";
    private int puerto = 4444;


    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getDir_ip() {
        return dir_ip;
    }

    public void setDir_ip(String dir_ip) {
        this.dir_ip = dir_ip;
    }



    public Preferencias(String dir_ip){
        setDir_ip(dir_ip);

    }
}
