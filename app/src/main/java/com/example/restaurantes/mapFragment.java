package com.example.restaurantes;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap map;
    private MapView mapView;
    private GoogleMap googleMap;

    LocationManager locationManager;
    LocationListener locationListener;
    private Location currentLocation;

    Map<Marker, ModeloDatoRestaurante> mapaRestaurantesUbicaciones;
    public static ModeloDatoRestaurante restauranteSeleccionado;

    public mapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapaRestaurantesUbicaciones = new HashMap<>();
        currentLocation = null;
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("location", String.format("Latitud: %f\nLongitud: %f", location.getLatitude(),location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        Log.e("activity", getActivity().getClass().getSimpleName());
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

                restauranteSeleccionado = mapaRestaurantesUbicaciones.get(marker);
                Log.e("macador", restauranteSeleccionado.getNombre());
                Intent intent = new Intent(getContext(), DetallesActivity.class);
                intent.putExtra("action", "verMarcador");
                startActivity(intent);
            }
        });

        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            for (ModeloDatoRestaurante restaurante: MainActivity.restaurantes){
                LatLng ubicacion = new LatLng(restaurante.getLatitud(), restaurante.getLongitud());
                MarkerOptions markerOption = new MarkerOptions().position(ubicacion).title(restaurante.getNombre());
                Marker marker = map.addMarker(markerOption);
                mapaRestaurantesUbicaciones.put(marker, restaurante);

            }
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
            /*Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationManager.requestSingleUpdate(criteria, locationListener, null);*/
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            // revisar como actualizar la posición

            LatLng posicionActual = null;
            if (LoginActivity.ubicacionActual != null){
                Log.e("location", " ubicacion no es null");
                posicionActual = new LatLng(LoginActivity.ubicacionActual.getLatitude(), LoginActivity.ubicacionActual.getLongitude());
            } else {
                Log.e("location", " ubicacion es null");
                posicionActual = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionActual,15));
        } else {
            ModeloDatoRestaurante restaurante = DetallesActivity.restauranteSeleccionado;
            LatLng ubicacion = new LatLng(restaurante.getLatitud(), restaurante.getLongitud());
            MarkerOptions markerOption = new MarkerOptions().position(ubicacion).title(restaurante.getNombre());
            Marker marker = map.addMarker(markerOption);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15));
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("macador", "entro a onMarkerClick");
        return false;
    }
}
