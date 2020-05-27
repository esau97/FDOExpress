package com.example.fdoexpress.Adapter;

import java.io.Serializable;

public class HistoryPedido implements Serializable {
    private String descripcion;
    private String estado;
    private String fecha;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public HistoryPedido(String descripcion, int estado, String fecha) {
        this.descripcion = descripcion;
        comprobarEstado(estado);
        this.fecha = fecha;

    }

    public HistoryPedido() {
    }

    public void comprobarEstado(int codEstado){
        switch (codEstado){
            case 1:
                setEstado("Pendiente");
                break;
            case 2:
                setEstado("Camino a instalaciones");
                break;
            case 3:
                setEstado("En Instalaciones");
                break;
            case 4:
                setEstado("En reparto");
                break;
            case 5:
                setEstado("Entregado");
                break;
        }
    }
}
