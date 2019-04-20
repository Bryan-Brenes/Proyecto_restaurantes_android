package com.example.restaurantes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/*
* Este es el modelo de datos que tiene un restaurante para poder ser desplegado por el listview
* */

public class ModeloDatoRestaurante {
    private String nombre;
    private String ubicacion;
    private Bitmap calificacion;
    private Bitmap imagen;
    private Context context;

    private double latitud;
    private double longitud;
    private ArrayList<String> foods;
    private String id;
    private int precio;
    private int numero;
    private String paginaWeb;
    private ArrayList<DiaHorario> horario;

    public ModeloDatoRestaurante(Context context, String nombre, String ubicacion, int calificacion, Bitmap imagen) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.imagen = imagen;
        this.context = context;

        if (imagen == null){
            this.imagen = BitmapFactory.decodeResource(context.getResources(), R.drawable.rest_image_default);
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

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public Bitmap getCalificacion() {
        return calificacion;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public ArrayList<String> getFoods() {
        return foods;
    }

    public String getIdRestaurante() {
        return id;
    }

    public int getPrecio() {
        return precio;
    }

    public int getNumero() {
        return numero;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public ArrayList<DiaHorario> getHorario() {
        return horario;
    }

    // setters

    public void setContextRestaurante(Context context){
        this.context = context;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setFoods(ArrayList<String> foods) {
        this.foods = foods;
    }

    public void setIdRestaurante(String id) {
        this.id = id;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public void setHorario(ArrayList<DiaHorario> horario) {
        this.horario = horario;
    }
}
