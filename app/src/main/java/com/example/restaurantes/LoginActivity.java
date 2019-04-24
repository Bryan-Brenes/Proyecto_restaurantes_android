package com.example.restaurantes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    public static final String MIXPANEL_TOKEN = "d45f6bb62c956f7c15c973e7e5ed1668";

    private MixpanelAPI mixpanel;

    LocationManager locationManager;
    LocationListener locationListener;

    public static Location ubicacionActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mixpanel = MixpanelAPI.getInstance(getApplicationContext(), LoginActivity.MIXPANEL_TOKEN);
        setContentView(R.layout.login_activity);

        ubicacionActual = null;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("location", String.format("Latitud: %f\nLongitud: %f", location.getLatitude(),location.getLongitude()));
                ubicacionActual = location;
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        if(mixpanel != null) {
            mixpanel.flush();
        }
        super.onDestroy();
    }

    public void abrirPantallas(View view){

        /*JSONObject obj = new JSONObject();
        try{
            obj.put("token", "1235");
            obj.put("email","1234");

            Log.e("test", obj.toString());

            Post_json post = new Post_json();
            DatosConsulta datos = new DatosConsulta(Post_json.OBTENER_COMIDAS, obj);
            JSONObject res = post.execute(datos).get();

            if (res != null){
                Log.e("url", res.toString());
            } else {
                Log.e("url", "respuesta nula");
            }


        } catch (JSONException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        }*/

        int idButton = view.getId();

        if (idButton == R.id.loginBtn){
            Intent intent = new Intent(this, user_login.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, user_signup.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                Log.e("location", "entro a onRequestPermission");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("location", "entro al if de on request");
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.e("location", "Si ddieron el permiso");
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);
                    }
                }
        }
    }
}
