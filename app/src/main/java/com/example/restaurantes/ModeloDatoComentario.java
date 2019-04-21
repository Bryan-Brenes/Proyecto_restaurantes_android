package com.example.restaurantes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ModeloDatoComentario {
    private Bitmap imagen;
    private String nombre;
    private String comentario;
    private Bitmap calificacion;
    private int calificacionNumerica;
    private int precio;
    private String fecha;
    private Context context;

    public ModeloDatoComentario(Context context, Bitmap imagen, String nombre, String comentario, int calificacion, int precio, String fecha) {
        this.calificacionNumerica = calificacion;
        this.context = context;
        this.imagen = imagen;
        this.nombre = nombre;
        this.comentario = comentario;
        this.precio = precio;
        this.fecha = fecha;

        if (this.imagen == null){
            this.imagen = BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        }

        switch (calificacion){
            case 0:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.none_of_five_64);
                break;
            case 1:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.one_of_five_64);
                break;
            case 2:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.two_of_five_64);
                break;
            case 3:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.three_of_five_64);
                break;
            case 4:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.four_of_five_64);
                break;
            case 5:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.five_of_five_64);
                break;
            default:
                this.calificacion = BitmapFactory.decodeResource(context.getResources(), R.drawable.none_of_five_64);
        }
    }

    public ModeloDatoComentario(){

    }

    public int getCalificacionNumerica() {
        return calificacionNumerica;
    }

    public void setCalificacionNumerica(int calificacionNumerica) {
        this.calificacionNumerica = calificacionNumerica;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getNombreUsuario() {
        return nombre;
    }

    public void setNombreUsuario(String nombre) {
        this.nombre = nombre;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Bitmap getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Bitmap calificacion) {
        this.calificacion = calificacion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
