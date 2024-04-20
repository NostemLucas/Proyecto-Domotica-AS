package com.lemtproyecto.casadomotica.modelos;

public class Usuarios {

    private String correo;
    private String nombre;
    private String apellidos;
    private String imagen;


    public Usuarios(String correo, String nombre, String apellidos, String imagen) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.imagen = imagen;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}




