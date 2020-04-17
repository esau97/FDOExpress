package Util;

public class Preferencias {
    private String dir_ip="192.168.1.39";
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

    public Preferencias(){

    }
}
