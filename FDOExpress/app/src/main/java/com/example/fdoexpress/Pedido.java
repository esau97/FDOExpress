package com.example.fdoexpress;

import java.io.Serializable;

public class Pedido implements Serializable {
    private String proveedor;
    private String estado;
    private String fecha;
    private String cSeguimiento;

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
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

    public String getcSeguimiento() {
        return cSeguimiento;
    }

    public void setcSeguimiento(String cSeguimiento) {
        this.cSeguimiento = cSeguimiento;
    }

    public Pedido(String proveedor, int estado, String fecha, String cSeguimiento) {
        this.proveedor = proveedor;
        comprobarEstado(estado);
        this.fecha = fecha;
        this.cSeguimiento = cSeguimiento;
    }

    public Pedido() {
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