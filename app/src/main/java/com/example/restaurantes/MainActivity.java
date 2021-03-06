package com.example.restaurantes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    /*private String token;
    private String email;
    private String nombre;*/
    public static ArrayList<ModeloDatoRestaurante> restaurantes;
    public static Location ubicacionActual;

    private MixpanelAPI mixpanel;

    LocationManager locationManager;
    LocationListener locationListener;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ubicacionActual = null;

        mixpanel = MixpanelAPI.getInstance(getApplicationContext(), LoginActivity.MIXPANEL_TOKEN);

        JSONObject props = new JSONObject();
        try {
            props.put("Usuario", SessionManager.getEmail());
            props.put("Actividad", "ListaRestaurantes");
            mixpanel.track("Movimiento", props);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        restaurantes = new ArrayList<>();

        /*token = getIntent().getStringExtra("token");
        nombre = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Aqui va el intent para ir a la pantalla de nuevo restaurante
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("fab", "Se presionó agregar nuevo restaurante");
                Intent intent = new Intent(getApplicationContext(), NuevoRestauranteActivity.class);
                /*intent.putExtra("token", token);
                intent.putExtra("name", nombre);
                intent.putExtra("email", email);*/
                intent.putExtra("accion", "nuevo");
                startActivity(intent);
            }
        });

        obtenerRestaurantes();

    }

    @Override
    protected void onDestroy() {
        if(mixpanel != null) {
            mixpanel.flush();
        }
        super.onDestroy();
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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    }
                }
        }
    }

    private void obtenerRestaurantes(){

        JSONObject obj = new JSONObject();
        try{
            obj.put("token", SessionManager.getToken());
            obj.put("email", SessionManager.getEmail());
            JSONObject filters = FiltersActivity.getFilters(0, 0,this);
            obj.put("filters", filters);
            System.out.println(filters);
            Post_json post = new Post_json();
            DatosConsulta datos = new DatosConsulta(Post_json.OBTENER_RESTAURANTES, obj);
            mixpanel.timeEvent("Obtener restaurantes");
            JSONObject res = post.execute(datos).get();
            System.out.println(res);
            mixpanel.track("Obtener Restaurantes");

            if (res != null){
                Log.e("rest", res.toString());
                // se verifica que no haya un error con la consulta -------------------------------------------------
                String status = Post_json.verificarSiTieneStatus(res);
                if (status.equals("Successfull")){

                    // se actualiza el token de la sesion
                    String tokenNuevo = res.getString("token");
                    SessionManager.setToken(tokenNuevo);

                    // se pobla es arrayList de restaurantes
                    JSONArray restaurantesJsonArray = res.getJSONArray("restaurants");
                    for (int i = 0; i < restaurantesJsonArray.length(); i++){

                        JSONObject restActual = restaurantesJsonArray.getJSONObject(i);
                        // obtener nombre
                        String nombre = restActual.getString("name");

                        // obtener direccion
                        JSONObject address = restActual.getJSONObject("address");
                        double latitud = address.getDouble("lat");
                        double longitud = address.getDouble("long");
                        String direccion = address.getString("direction");

                        // obtener foods
                        ArrayList<String> foods = new ArrayList<>();
                        try {
                            JSONArray foodsArray = restActual.getJSONArray("foods");
                            for (int j = 0; j < foodsArray.length(); j++){
                                foods.add(foodsArray.getString(j));
                            }
                        }
                        catch (Exception e) {

                        }

                        // obtener id
                        String id = restActual.getString("_id");

                        // obtener calificacion
                        int calificacion = restActual.getInt("calification");

                        // obtener precio
                        int precio = restActual.getInt("price");

                        // obtener numero
                        int numero = 0;
                        try {
                            numero = restActual.getInt("number");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        // obtener pagina web
                        String paginaWeb = restActual.getString("webPage");
                        if (paginaWeb.equals("null")){
                            paginaWeb = null;
                        }

                        // obtener horarios
                        JSONArray horariosArray = null;
                        ArrayList<DiaHorario> horarios = new ArrayList<>();
                        try {
                            horariosArray = restActual.getJSONArray("schedules");
                            for (int k = 0; k < horariosArray.length(); k++) {
                                JSONObject horarioActual = horariosArray.getJSONObject(k);

                                String diaHorario = horarioActual.getString("day");
                                String aperturaHorario = horarioActual.getString("open");
                                String cierreHorario = horarioActual.getString("close");

                                DiaHorario dia = new DiaHorario(diaHorario, aperturaHorario, cierreHorario);
                                horarios.add(dia);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        ModeloDatoRestaurante rest = new ModeloDatoRestaurante(this, nombre, direccion, calificacion, null);
                        rest.setLatitud(latitud);
                        rest.setLongitud(longitud);
                        rest.setFoods(foods);
                        rest.setIdRestaurante(id);
                        rest.setPrecio(precio);
                        rest.setNumero(numero);
                        rest.setPaginaWeb(paginaWeb);
                        rest.setHorario(horarios);
                        restaurantes.add(rest);
                    }

                } else {
                    Toast.makeText(this, "No se pudo cargar restaurantes", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("url", "respuesta nula");
            }

        } catch (JSONException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (ExecutionException e){
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (Exception e){
            e.printStackTrace();
        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.filters) {
            Intent intent = new Intent(this, FiltersActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            System.out.println("Position: " + String.valueOf(position));

            //return PlaceholderFragment.newInstance(position + 1);

            switch (position){
                case 0:
                    mapFragment mapFragment = new mapFragment();
                    return mapFragment;
                case 1:
                    RestListTabFragment restListFragment = new RestListTabFragment();
                    return restListFragment;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
