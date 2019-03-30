package com.example.restaurantes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap map;
    private MapView mapView;
    private GoogleMap googleMap;

    public mapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map1);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("mapa", "entro a onMapReady");
        map = googleMap;

        // este método se ejecuta cuando se le da click a un marcador
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("macador", "entro a onMarkerClick2");
                return false;
            }
        });

        // Este método se ejecuta cuando se le da click a la etiqueta con el nombre que se muestra
        // cuando se le da click a un marcador
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("macador", "entro a infowindowClick");
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng woods = new LatLng(9.8786792,-83.9113044);
        map.addMarker(new MarkerOptions().position(woods).title("Woods Restaurante"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(woods,16));

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("macador", "entro a onMarkerClick");
        return false;
    }
}
