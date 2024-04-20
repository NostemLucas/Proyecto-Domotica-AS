package com.lemtproyecto.casadomotica.modelos;

public class Cambios {
    private String fecha;
    private String correo;
    private String modificacion;

    public Cambios(String fecha, String correo, String modificacion) {
        this.fecha = fecha;
        this.correo = correo;
        this.modificacion = modificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getModificacion() {
        return modificacion;
    }

    public void setModificacion(String modificacion) {
        this.modificacion = modificacion;
    }
}
