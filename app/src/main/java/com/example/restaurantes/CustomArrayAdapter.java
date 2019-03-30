package com.example.restaurantes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<ModeloDatoRestaurante> {

    ArrayList<ModeloDatoRestaurante> listaRestaurantesModel;
    Context context;

    private static class ViewHolder{
        ImageView imagenRest;
        TextView nombreRest;
        TextView ubicacionRest;
        ImageView imagenCalificacion;
    }

    public CustomArrayAdapter(Context context, ArrayList<ModeloDatoRestaurante> elementos){
        super(context, R.layout.rest_row_layout, elementos);
        this.listaRestaurantesModel = elementos;
        this.context = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModeloDatoRestaurante dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rest_row_layout, parent, false);
            viewHolder.nombreRest = (TextView) convertView.findViewById(R.id.nombreRestaurante);
            viewHolder.ubicacionRest = (TextView) convertView.findViewById(R.id.ubicacionRestaurante);
            viewHolder.imagenCalificacion = (ImageView) convertView.findViewById(R.id.imagenCalificacion);
            viewHolder.imagenRest = (ImageView) convertView.findViewById(R.id.imagenRestaurante);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.nombreRest.setText(dataModel.getNombre());
        viewHolder.nombreRest.setTextColor(Color.BLACK);
        viewHolder.ubicacionRest.setText(dataModel.getUbicacion());
        viewHolder.ubicacionRest.setTextColor(Color.BLACK);
        viewHolder.imagenRest.setImageBitmap(dataModel.getImagen());
        viewHolder.imagenCalificacion.setImageBitmap(dataModel.getCalificacion());

        // Return the completed view to render on screen
        return convertView;
    }

}
