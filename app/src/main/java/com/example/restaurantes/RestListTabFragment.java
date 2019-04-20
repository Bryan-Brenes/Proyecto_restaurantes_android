package com.example.restaurantes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class RestListTabFragment extends Fragment {
    private static final String TAG = "RestListFragment";

    private ListView listView;
    public static ModeloDatoRestaurante restauranteSeleccionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_list_tab_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.restaurantListView);

        ArrayList<ModeloDatoRestaurante> elementos = MainActivity.restaurantes;

        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity().getApplicationContext(), elementos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // mostrar detalles
                restauranteSeleccionado = MainActivity.restaurantes.get(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), DetallesActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position: " + String.valueOf(position));
                Intent intent = new Intent(getActivity().getApplicationContext(), NuevoRestauranteActivity.class);
                intent.putExtra("accion", "ver");
                restauranteSeleccionado = MainActivity.restaurantes.get(position);
                startActivity(intent);
                return true; // true es para consumir el evento de click normal
            }
        });
    }
}
