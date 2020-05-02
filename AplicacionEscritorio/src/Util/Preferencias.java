package Util;

import java.io.IOException;
import java.net.Socket;

public class Preferencias {
    private String dir_ip="192.168.1.39";
    private int puerto = 4444;
    private Socket socket;

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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Preferencias(){
        try {
            socket = new Socket(getDir_ip(),getPuerto());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
