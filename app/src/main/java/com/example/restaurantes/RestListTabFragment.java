package com.example.restaurantes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        ArrayList<ModeloDatoRestaurante> elementos = new ArrayList<>();
        elementos.add(new ModeloDatoRestaurante(getActivity().getApplicationContext(), "Woods", "Cartago", 4, null));
        elementos.add(new ModeloDatoRestaurante(getActivity().getApplicationContext(), "Subway", "Cartago", 5, null));
        elementos.add(new ModeloDatoRestaurante(getActivity().getApplicationContext(), "Moe's", "Cartago", 5, null));
        elementos.add(new ModeloDatoRestaurante(getActivity().getApplicationContext(), "Treinta y tantos", "Turrialba", 2, null));

        CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity().getApplicationContext(), elementos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position: " + String.valueOf(position));
            }
        });
    }
}
