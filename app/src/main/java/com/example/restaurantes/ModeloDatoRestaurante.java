package com.example.restaurantes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
* Este es el modelo de datos que tiene un restaurante para poder ser desplegado por el listview
* */

public class ModeloDatoRestaurante {
    private String nombre;
    private String ubicacion;
    private Bitmap calificacion;
    private Bitmap imagen;

    private Context context;

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
}
