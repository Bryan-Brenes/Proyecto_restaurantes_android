package com.example.restaurantes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayAdapterComentarios extends ArrayAdapter<ModeloDatoComentario> {

    ArrayList<ModeloDatoComentario> listaComentariosModel;
    Context context;

    private static class ViewHolder{
        ImageView imagenUsuario;
        TextView nombreUsuario;
        TextView comentario;
        ImageView imagenCalificacion;
    }

    public CustomArrayAdapterComentarios(Context context, ArrayList<ModeloDatoComentario> elementos){
        super(context, R.layout.comment_row_layout, elementos);
        this.listaComentariosModel = elementos;
        this.context = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModeloDatoComentario dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        CustomArrayAdapterComentarios.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new CustomArrayAdapterComentarios.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comment_row_layout, parent, false);
            viewHolder.nombreUsuario = (TextView) convertView.findViewById(R.id.nombreUsuario);
            viewHolder.comentario = (TextView) convertView.findViewById(R.id.comentarioUsuario);
            viewHolder.imagenCalificacion = (ImageView) convertView.findViewById(R.id.imagenCalificacion);
            viewHolder.imagenUsuario = (ImageView) convertView.findViewById(R.id.imagenUsuario);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomArrayAdapterComentarios.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.nombreUsuario.setText(dataModel.getNombreUsuario());
        viewHolder.nombreUsuario.setTextColor(Color.BLACK);
        viewHolder.comentario.setText(dataModel.getComentario());
        viewHolder.comentario.setTextColor(Color.BLACK);
        viewHolder.imagenUsuario.setImageBitmap(dataModel.getImagen());
        viewHolder.imagenCalificacion.setImageBitmap(dataModel.getCalificacion());

        // Return the completed view to render on screen
        return convertView;
    }
}
