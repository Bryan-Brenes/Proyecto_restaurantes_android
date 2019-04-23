package com.example.restaurantes;

import org.apache.commons.io.IOUtils;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NuevoRestauranteActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final int PICK_IMAGE = 100;
    private static final int GET_IMAGES = 3500;
    /*private String token;
    private String email;
    private String nombre;*/
    private String accion;
    private String id;

    private Location currentLocation;

    private final int LOCATION_REFRESH_TIME = 2500;
    private final int LOCATION_REFRESH_DISTANCE = 0;

    private EditText editTextSeleccionado;

    LocationManager locationManager;
    LocationListener locationListener;

    ImageSwitcher switcher;
    ImageButton editImageButton;
    ImageButton openEditButton;
    EditText nombreEditText;
    EditText direccionEditText;
    EditText telefonoEditText;
    EditText paginaWebEditText;
    EditText lunesAbreEditText;
    EditText lunesCierraEditText;
    EditText martesAbreEditText;
    EditText martesCierraEditText;
    EditText miercolesAbreEditText;
    EditText miercolesCierraEditText;
    EditText juevesAbreEditText;
    EditText juevesCierraEditText;
    EditText viernesAbreEditText;
    EditText viernesCierraEditText;
    EditText sabadoAbreEditText;
    EditText sabadoCierraEditText;
    EditText domingoAbreEditText;
    EditText domingoCierraEditText;
    LinearLayout tipoComidaLinearLayout;

    Button agregarRestauranteBtn;


    ArrayList<String> tiposComida;
    ArrayList<CheckBox> checkBoxesTiposComida;

    String fieldName = "photos";
    String nameFile = "upload";
    int maxFiles = 5;
    ArrayList<File> files = new ArrayList<>();
    ArrayList<Uri> images = new ArrayList<>();
    int currentIndex = 0;
    Handler handler;

    ArrayList<Uri> imagenesURIArrayList;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_restaurante);

        /*token = getIntent().getStringExtra("token");
        nombre = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");*/
        accion = getIntent().getStringExtra("accion");
        System.out.println(accion);

        imagenesURIArrayList = new ArrayList<>();

        currentLocation = null;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //currentLocation = location;
                Log.e("location", "entro");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, locationListener);
        }

        switcher = findViewById(R.id.switcher);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                return imageView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);

        switcher.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if(images.size() > 0) {
              currentIndex++;
              if (currentIndex >= images.size()) {
                currentIndex = 0;
              }
              switcher.setImageURI(images.get(currentIndex));
            }
          }
        });

        switcher.setImageResource(R.drawable.store);

        id = null;
        if (accion.equals("actualizar")){
            id = getIntent().getStringExtra("id");
        }

        openEditButton = findViewById(R.id.openEditButton);
        agregarRestauranteBtn = findViewById(R.id.agregarBtn);
        editImageButton = findViewById(R.id.editImageButton);
        nombreEditText = findViewById(R.id.nombreRestauranteEditText);
        direccionEditText = findViewById(R.id.direccionRestauranteEditText);
        telefonoEditText = findViewById(R.id.telefonoRestauranteEditText);
        paginaWebEditText = findViewById(R.id.paginaWebRestauranteEditText);
        lunesAbreEditText = findViewById(R.id.lunesAbreEditText);
        lunesCierraEditText = findViewById(R.id.lunesCierraEditText);
        martesAbreEditText = findViewById(R.id.martesAbreEditText);
        martesCierraEditText = findViewById(R.id.martesCierraEditText);
        miercolesAbreEditText = findViewById(R.id.miercolesAbreEditText);
        miercolesCierraEditText = findViewById(R.id.miercolesCierraEditText);
        juevesAbreEditText = findViewById(R.id.juevesAbreEditText);
        juevesCierraEditText = findViewById(R.id.juevesCierraEditText);
        viernesAbreEditText = findViewById(R.id.viernesAbreEditText);
        viernesCierraEditText = findViewById(R.id.viernesCierraEditText);
        sabadoAbreEditText = findViewById(R.id.sabadoAbreEditText);
        sabadoCierraEditText = findViewById(R.id.sabadoCierraEditText);
        domingoAbreEditText = findViewById(R.id.domingoAbreEditText);
        domingoCierraEditText = findViewById(R.id.domingoCierraEditText);
        tipoComidaLinearLayout = findViewById(R.id.comboBoxVerticalLayout);

        tiposComida = new ArrayList<>();
        checkBoxesTiposComida = new ArrayList<>();

        obtenerTiposComida();
        agregarLosTiposAInterfaz();
        habilitarEdicion(accion);

        if (accion.equals("ver") || accion.equals("actualizar")){
            poblarFormulario();
        }

        openEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habilitarEdicion("actualizar");
            }
        });
    }

    public void sendImages(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            startActivityForResult(intent, GET_IMAGES);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case PICK_IMAGE:
                    imageURI = data.getData();
                    imagenesURIArrayList.add(imageURI);
                    break;
                case GET_IMAGES:
                    files.clear();
                    images.clear();
                    obtainImages(data);
                    break;
            }
        }
    }

    private void obtainImages(Intent data) {
        if(data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for(int i = 0; i < count; i++) {
                selectImage(data.getClipData().getItemAt(i).getUri(), i);
            }
        }
        else if(data.getData() != null) {
            selectImage(data.getData(), 0);
        }
    }

    private void selectImage(Uri uri, int number) {
        OutputStream outputStream = null;
        try {
            if(files.size() < maxFiles) {
                InputStream is = getContentResolver().openInputStream(uri);
                File f = new File(getExternalFilesDir(null), nameFile + number + ".jpg");
                outputStream = new FileOutputStream(f);
                IOUtils.copy(is, outputStream);
                files.add(f);
                images.add(uri);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void upload() {
      if(files.size() > 0) {
        Builders.Any.B builder = Ion.with(NuevoRestauranteActivity.this).load("POST", Post_json.ANADIR_FOTOS);
        for (int i = 0; i < files.size(); i++) {
          builder.setMultipartFile(fieldName, files.get(i));
        }
        builder.setMultipartParameter("id", id);
        builder.setMultipartParameter("token", SessionManager.getToken());
        builder.setMultipartParameter("email", SessionManager.getEmail());
        Future uploading = builder
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
              @Override
              public void onCompleted(Exception e, JsonObject result) {
                if(result != null) {
                  JsonElement element = result.get("status");
                  String status = element.getAsString();
                  if(status.equals("Photos added")) {
                    JsonElement otherElement = result.get("token");
                    String token = otherElement.getAsString();
                    SessionManager.setToken(token);
                    Toast.makeText(getApplicationContext(), "Fotos añadidas", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                  }
                  else {
                    Toast.makeText(getApplicationContext(), "Fallo al subir las fotografias", Toast.LENGTH_SHORT).show();
                  }
                }
                else {
                  Toast.makeText(getApplicationContext(), "Fallo al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
              }
            });
      }
      else {
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
                    }
                }
        }
    }

    private void habilitarEdicion(String action){
        // deshabilitar edicion
        if (action.equals("nuevo")){
            openEditButton.setVisibility(View.GONE);
            editImageButton.setVisibility(View.VISIBLE);
            nombreEditText.setEnabled(true);
            direccionEditText.setEnabled(true);
            telefonoEditText.setEnabled(true);
            paginaWebEditText.setEnabled(true);
            lunesAbreEditText.setEnabled(true);
            lunesCierraEditText.setEnabled(true);
            martesAbreEditText.setEnabled(true);
            martesCierraEditText.setEnabled(true);
            miercolesAbreEditText.setEnabled(true);
            miercolesCierraEditText.setEnabled(true);
            juevesAbreEditText.setEnabled(true);
            juevesCierraEditText.setEnabled(true);
            viernesAbreEditText.setEnabled(true);;
            viernesCierraEditText.setEnabled(true);
            sabadoAbreEditText.setEnabled(true);
            sabadoCierraEditText.setEnabled(true);
            domingoAbreEditText.setEnabled(true);
            domingoCierraEditText.setEnabled(true);
            for(CheckBox checkBox: checkBoxesTiposComida){
                checkBox.setEnabled(true);
            }
        } else if(action.equals("actualizar")){
            editImageButton.setVisibility(View.VISIBLE);
            nombreEditText.setEnabled(true);
            direccionEditText.setEnabled(false);
            telefonoEditText.setEnabled(true);
            paginaWebEditText.setEnabled(true);
            lunesAbreEditText.setEnabled(true);
            lunesCierraEditText.setEnabled(true);
            martesAbreEditText.setEnabled(true);
            martesCierraEditText.setEnabled(true);
            miercolesAbreEditText.setEnabled(true);
            miercolesCierraEditText.setEnabled(true);
            juevesAbreEditText.setEnabled(true);
            juevesCierraEditText.setEnabled(true);
            viernesAbreEditText.setEnabled(true);;
            viernesCierraEditText.setEnabled(true);
            sabadoAbreEditText.setEnabled(true);
            sabadoCierraEditText.setEnabled(true);
            domingoAbreEditText.setEnabled(true);
            domingoCierraEditText.setEnabled(true);
            for(CheckBox checkBox: checkBoxesTiposComida){
                checkBox.setEnabled(true);
            }

        } else  {
            editImageButton.setVisibility(View.GONE);
            nombreEditText.setEnabled(false);
            direccionEditText.setEnabled(false);
            telefonoEditText.setEnabled(false);
            paginaWebEditText.setEnabled(false);
            lunesAbreEditText.setEnabled(false);
            lunesCierraEditText.setEnabled(false);
            martesAbreEditText.setEnabled(false);
            martesCierraEditText.setEnabled(false);
            miercolesAbreEditText.setEnabled(false);
            miercolesCierraEditText.setEnabled(false);
            juevesAbreEditText.setEnabled(false);
            juevesCierraEditText.setEnabled(false);
            viernesAbreEditText.setEnabled(false);
            viernesCierraEditText.setEnabled(false);
            sabadoAbreEditText.setEnabled(false);
            sabadoCierraEditText.setEnabled(false);
            domingoAbreEditText.setEnabled(false);
            domingoCierraEditText.setEnabled(false);
            for(CheckBox checkBox: checkBoxesTiposComida){
                checkBox.setEnabled(false);
            }
        }

        if(action.equals("nuevo")){
            agregarRestauranteBtn.setText("agregar Restaurante");
            agregarRestauranteBtn.setVisibility(View.VISIBLE);
        } else if(action.equals("actualizar")){
            agregarRestauranteBtn.setText("Actualizar");
            agregarRestauranteBtn.setVisibility(View.VISIBLE);
        } else{
            agregarRestauranteBtn.setVisibility(View.INVISIBLE);
        }

    }

    private void obtenerTiposComida(){

        // se realiza la solicitud para obtener los tipos de comida
        JSONObject obj = new JSONObject();
        try{

            obj.put("token", SessionManager.getToken());
            obj.put("email", SessionManager.getEmail());

            Post_json post = new Post_json();
            DatosConsulta datos = new DatosConsulta(Post_json.OBTENER_COMIDAS, obj);
            JSONObject res = post.execute(datos).get();

            if (res != null){
                Log.e("comidas", res.toString());
                // se verifica que no haya un error con la consulta -------------------------------------------------
                String status = Post_json.verificarSiTieneStatus(res);
                if (!status.equals("Successful")){
                    Toast.makeText(this, "No se pudo cargar los tipos de comida", Toast.LENGTH_SHORT).show();
                } else {

                    SessionManager.setToken(res.getString("token")); // se actualiza el token de la sesión
                    JSONArray jsonComidas = res.getJSONArray("foods");
                    if (jsonComidas != null){
                        for (int i = 0; i < jsonComidas.length(); i++){
                            tiposComida.add(jsonComidas.getString(i));
                        }
                    } else {
                        Log.e("comidas", "Json Array de tipo de comidas nulo");
                    }

                }
            } else {
                Log.e("url", "respuesta nula");
            }

            for (String str : tiposComida){
                Log.e("comidas", str);
            }


        } catch (JSONException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void agregarLosTiposAInterfaz(){
        if (tiposComida.size() != 0){
            for (String tipo : tiposComida){
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(tipo);
                tipoComidaLinearLayout.addView(checkBox);
                checkBoxesTiposComida.add(checkBox);
            }
        }
    }

    public void agregarRestaurante(View view){



        if (accion.equals("nuevo")){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (currentLocation == null){
                Toast.makeText(this, "No se pudo obtener su localización", Toast.LENGTH_SHORT).show();
                return;
            }

            // agregar el restaurante a la base de datos

            if (nombreEditText.getText().toString().equals("") || direccionEditText.getText().toString().equals("")){
                Toast.makeText(this, "Los campos nombre y dirección son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject obj = new JSONObject();
            try{

                // armando el json de consulta
                obj.put("token", SessionManager.getToken());
                obj.put("email", SessionManager.getEmail());
                obj.put("name", nombreEditText.getText().toString());

                // json de direccion
                JSONObject direccion = new JSONObject();
                direccion.put("lat", currentLocation.getLatitude());
                direccion.put("long", currentLocation.getLongitude());
                direccion.put("direction", direccionEditText.getText().toString());
                obj.put("address", direccion);

                // se agrega telefono al json solo si el usuario ingresó un telefono
                if (!telefonoEditText.getText().toString().equals("")){
                    obj.put("number", Integer.valueOf(telefonoEditText.getText().toString()));
                }

                // se agrega página web solo si el usuario lo indicó
                if (!paginaWebEditText.getText().toString().equals("")){
                    obj.put("webPage", paginaWebEditText.getText().toString());
                }

                // Array de comidas
                JSONArray tipoComidasSeleccionadas = new JSONArray();
                for (CheckBox checkBox: checkBoxesTiposComida){
                    if (checkBox.isChecked()){
                        tipoComidasSeleccionadas.put(checkBox.getText().toString());
                    }
                }
                if (tipoComidasSeleccionadas.length() != 0){
                    obj.put("foods", tipoComidasSeleccionadas);
                }

                // Array de horarios
                JSONArray horarioArray = new JSONArray();

                // boolean para solo agregar horarios si todos los campos estan llenos
                boolean algunCampoHorarioVacio = false;
                Long horaApertura = 0L;
                Long horaCierre = 0L;

                // json para lunes
                JSONObject lunesHorario = new JSONObject();
                if (lunesAbreEditText.getText().toString().equals("") || lunesCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(lunesAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(lunesCierraEditText.getText().toString());
                lunesHorario.put("day", "Lunes");
                lunesHorario.put("open", horaApertura);
                lunesHorario.put("close", horaCierre);
                horarioArray.put(lunesHorario);

                // json para martes
                JSONObject martesHorario = new JSONObject();
                if (martesAbreEditText.getText().toString().equals("") || martesCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(martesAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(martesCierraEditText.getText().toString());
                martesHorario.put("day", "Martes");
                martesHorario.put("open", horaApertura);
                martesHorario.put("close", horaCierre);
                horarioArray.put(martesHorario);

                // json para miercoles
                JSONObject miercolesHorario = new JSONObject();
                if (miercolesAbreEditText.getText().toString().equals("") || miercolesCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(miercolesAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(miercolesCierraEditText.getText().toString());
                miercolesHorario.put("day", "Miercoles");
                miercolesHorario.put("open", horaApertura);
                miercolesHorario.put("close", horaCierre);
                horarioArray.put(miercolesHorario);

                // json para jueves
                JSONObject juevesHorario = new JSONObject();
                if (juevesAbreEditText.getText().toString().equals("") || juevesCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(juevesAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(juevesCierraEditText.getText().toString());
                juevesHorario.put("day", "Jueves");
                juevesHorario.put("open", horaApertura);
                juevesHorario.put("close", horaCierre);
                horarioArray.put(juevesHorario);

                // json para viernes
                JSONObject viernesHorario = new JSONObject();
                if (viernesAbreEditText.getText().toString().equals("") || viernesCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(viernesAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(viernesCierraEditText.getText().toString());
                viernesHorario.put("day", "Viernes");
                viernesHorario.put("open", horaApertura);
                viernesHorario.put("close", horaCierre);
                horarioArray.put(viernesHorario);

                // json para sabado
                JSONObject sabadoHorario = new JSONObject();
                if (sabadoAbreEditText.getText().toString().equals("") || sabadoCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(sabadoAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(sabadoCierraEditText.getText().toString());
                sabadoHorario.put("day", "Sabado");
                sabadoHorario.put("open", horaApertura);
                sabadoHorario.put("close", horaCierre);
                horarioArray.put(sabadoHorario);

                // json para domingo
                JSONObject domingoHorario = new JSONObject();
                if (domingoAbreEditText.getText().toString().equals("") || domingoCierraEditText.getText().toString().equals("")){
                    algunCampoHorarioVacio = true;
                }
                horaApertura = getMinutesFromMidnight(domingoAbreEditText.getText().toString());
                horaCierre = getMinutesFromMidnight(domingoCierraEditText.getText().toString());
                domingoHorario.put("day", "Domingo");
                domingoHorario.put("open", horaApertura);
                domingoHorario.put("close", horaCierre);
                horarioArray.put(domingoHorario);

                if (!algunCampoHorarioVacio){
                    obj.put("schedules", horarioArray);
                }


                Log.e("rest", obj.toString());

                // falta hacer el request pero hay que ver el tipo de dato date si es necesario
                // ver si cuando se agrega un restaurante puede devolver el id para agregar imagenes

                Post_json post = new Post_json();
                DatosConsulta datos = new DatosConsulta(Post_json.CREAR_RESTAURANTE, obj);
                JSONObject res = post.execute(datos).get();

                if (res != null){
                    Log.e("rest", res.toString());
                    // se verifica que no haya un error con la consulta -------------------------------------------------
                    String status = Post_json.verificarSiTieneStatus(res);
                    if (status != null){

                        if(!status.equals("Restaurant added")){
                            Toast.makeText(this,"No se pudo agregar el restaurante", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,"Restaurante agregado", Toast.LENGTH_SHORT).show();
                            SessionManager.setToken(res.getString("token"));
                            id = res.getString("id");
                            upload();
                        }

                    } else {

                    }
                } else {
                    Log.e("url", "respuesta nula");
                }


            } catch (JSONException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else{
            // modificar el restaurante
            actualizarRestaurante();
        }
    }

    private void actualizarRestaurante(){
        JSONObject obj = new JSONObject();
        try{

            // armando el json de consulta
            obj.put("token", SessionManager.getToken());
            obj.put("email", SessionManager.getEmail());
            obj.put("id", RestListTabFragment.restauranteSeleccionado.getIdRestaurante());
            obj.put("name", nombreEditText.getText().toString());

            // se agrega telefono al json solo si el usuario ingresó un telefono
            if (!telefonoEditText.getText().toString().equals("")){
                obj.put("number", Integer.valueOf(telefonoEditText.getText().toString()));
            }

            // se agrega página web solo si el usuario lo indicó
            if (!paginaWebEditText.getText().toString().equals("")){
                obj.put("webPage", paginaWebEditText.getText().toString());
            }

            // Array de comidas
            JSONArray tipoComidasSeleccionadas = new JSONArray();
            for (CheckBox checkBox: checkBoxesTiposComida){
                if (checkBox.isChecked()){
                    tipoComidasSeleccionadas.put(checkBox.getText().toString());
                }
            }
            if (tipoComidasSeleccionadas.length() != 0){
                obj.put("foods", tipoComidasSeleccionadas);
            }

            // Array de horarios
            JSONArray horarioArray = new JSONArray();

            // boolean para solo agregar horarios si todos los campos estan llenos
            boolean algunCampoHorarioVacio = false;
            Long horaApertura = 0L;
            Long horaCierre = 0L;

            // json para lunes
            JSONObject lunesHorario = new JSONObject();
            if (lunesAbreEditText.getText().toString().equals("") || lunesCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(lunesAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(lunesCierraEditText.getText().toString());
            lunesHorario.put("day", "Lunes");
            lunesHorario.put("open", horaApertura);
            lunesHorario.put("close", horaCierre);
            horarioArray.put(lunesHorario);

            // json para martes
            JSONObject martesHorario = new JSONObject();
            if (martesAbreEditText.getText().toString().equals("") || martesCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(martesAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(martesCierraEditText.getText().toString());
            martesHorario.put("day", "Martes");
            martesHorario.put("open", horaApertura);
            martesHorario.put("close", horaCierre);
            horarioArray.put(martesHorario);

            // json para miercoles
            JSONObject miercolesHorario = new JSONObject();
            if (miercolesAbreEditText.getText().toString().equals("") || miercolesCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(miercolesAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(miercolesCierraEditText.getText().toString());
            miercolesHorario.put("day", "Miercoles");
            miercolesHorario.put("open", horaApertura);
            miercolesHorario.put("close", horaCierre);
            horarioArray.put(miercolesHorario);

            // json para jueves
            JSONObject juevesHorario = new JSONObject();
            if (juevesAbreEditText.getText().toString().equals("") || juevesCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(juevesAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(juevesCierraEditText.getText().toString());
            juevesHorario.put("day", "Jueves");
            juevesHorario.put("open", horaApertura);
            juevesHorario.put("close", horaCierre);
            horarioArray.put(juevesHorario);

            // json para viernes
            JSONObject viernesHorario = new JSONObject();
            if (viernesAbreEditText.getText().toString().equals("") || viernesCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(viernesAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(viernesCierraEditText.getText().toString());
            viernesHorario.put("day", "Viernes");
            viernesHorario.put("open", horaApertura);
            viernesHorario.put("close", horaCierre);
            horarioArray.put(viernesHorario);

            // json para sabado
            JSONObject sabadoHorario = new JSONObject();
            if (sabadoAbreEditText.getText().toString().equals("") || sabadoCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(sabadoAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(sabadoCierraEditText.getText().toString());
            sabadoHorario.put("day", "Sabado");
            sabadoHorario.put("open", horaApertura);
            sabadoHorario.put("close", horaCierre);
            horarioArray.put(sabadoHorario);

            // json para domingo
            JSONObject domingoHorario = new JSONObject();
            if (domingoAbreEditText.getText().toString().equals("") || domingoCierraEditText.getText().toString().equals("")){
                algunCampoHorarioVacio = true;
            }
            horaApertura = getMinutesFromMidnight(domingoAbreEditText.getText().toString());
            horaCierre = getMinutesFromMidnight(domingoCierraEditText.getText().toString());
            domingoHorario.put("day", "Domingo");
            domingoHorario.put("open", horaApertura);
            domingoHorario.put("close", horaCierre);
            horarioArray.put(domingoHorario);

            if (!algunCampoHorarioVacio){
                obj.put("schedules", horarioArray);
            }


            Log.e("rest", obj.toString());

            // falta hacer el request pero hay que ver el tipo de dato date si es necesario
            // ver si cuando se agrega un restaurante puede devolver el id para agregar imagenes

            Post_json post = new Post_json();
            DatosConsulta datos = new DatosConsulta(Post_json.ACTUALIZAR_RESTAURANTES, obj);
            JSONObject res = post.execute(datos).get();

            if (res != null){
                Log.e("rest", res.toString());
                // se verifica que no haya un error con la consulta -------------------------------------------------
                String status = Post_json.verificarSiTieneStatus(res);
                if (status != null){

                    if(!status.equals("Restaurant's updated")){
                        Toast.makeText(this,"No se pudo actualizar el restaurante", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,"Restaurante actualizado", Toast.LENGTH_SHORT).show();
                        SessionManager.setToken(res.getString("token"));
                        upload();
                    }

                }
            } else {
                Log.e("url", "respuesta nula");
            }


        } catch (JSONException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirGaleria(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    public void agregarFotos(){

    }

    public void mostrarTimePicker(View view){
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
        editTextSeleccionado = (EditText) view;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String tiempo = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
        editTextSeleccionado.setText(tiempo);
    }

    private void poblarFormulario(){

        //imagenRestauranteImageView.setEnabled(true);
        nombreEditText.setText(RestListTabFragment.restauranteSeleccionado.getNombre());
        direccionEditText.setText(RestListTabFragment.restauranteSeleccionado.getUbicacion());
        int numero = RestListTabFragment.restauranteSeleccionado.getNumero();
        if (numero == 0){
            telefonoEditText.setHint("No registrado");
        } else {
            telefonoEditText.setText(String.valueOf(numero));
        }
        String pagina = RestListTabFragment.restauranteSeleccionado.getPaginaWeb();
        if (pagina != null){
            paginaWebEditText.setText(pagina);
        } else {
            paginaWebEditText.setHint("No registrada");
        }

        ArrayList<DiaHorario> horario = RestListTabFragment.restauranteSeleccionado.getHorario();
        if(horario.size() != 0){
            /*String fechaApertura = dia.getHoraApertura();
              String horaApertura = fechaApertura.substring(11,16);*/

            // lunes
            lunesAbreEditText.setText(horario.get(0).getHoraApertura().substring(11,16));
            lunesCierraEditText.setText(horario.get(0).getHoraCierre().substring(11,16));

            // martes
            martesAbreEditText.setText(horario.get(1).getHoraApertura().substring(11,16));
            martesCierraEditText.setText(horario.get(1).getHoraCierre().substring(11,16));

            // miercoles
            miercolesAbreEditText.setText(horario.get(2).getHoraApertura().substring(11,16));
            miercolesCierraEditText.setText(horario.get(2).getHoraCierre().substring(11,16));

            // jueves
            juevesAbreEditText.setText(horario.get(3).getHoraApertura().substring(11,16));
            juevesCierraEditText.setText(horario.get(3).getHoraCierre().substring(11,16));

            // viernes
            viernesAbreEditText.setText(horario.get(4).getHoraApertura().substring(11,16));
            viernesCierraEditText.setText(horario.get(4).getHoraCierre().substring(11,16));

            // sabado
            sabadoAbreEditText.setText(horario.get(5).getHoraApertura().substring(11,16));
            sabadoCierraEditText.setText(horario.get(5).getHoraCierre().substring(11,16));

            // domingo
            domingoAbreEditText.setText(horario.get(6).getHoraApertura().substring(11,16));
            domingoCierraEditText.setText(horario.get(6).getHoraCierre().substring(11,16));
        }

        /*lunesAbreEditText.setEnabled(true);
        lunesCierraEditText.setEnabled(true);
        martesAbreEditText.setEnabled(true);
        martesCierraEditText.setEnabled(true);
        miercolesAbreEditText.setEnabled(true);
        miercolesCierraEditText.setEnabled(true);
        juevesAbreEditText.setEnabled(true);
        juevesCierraEditText.setEnabled(true);
        viernesAbreEditText.setEnabled(true);;
        viernesCierraEditText.setEnabled(true);
        sabadoAbreEditText.setEnabled(true);
        sabadoCierraEditText.setEnabled(true);
        domingoAbreEditText.setEnabled(true);
        domingoCierraEditText.setEnabled(true);*/

        ArrayList<String> foods = RestListTabFragment.restauranteSeleccionado.getFoods();
        if(foods.size() != 0){
            for(String food : foods){
                for(CheckBox checkBox : checkBoxesTiposComida){
                    if (checkBox.getText().toString().equals(food)){
                        checkBox.setChecked(true);
                    }
                }
            }
        }
    }

    private Long getMinutesFromMidnight(String tiempo){
        long minutosTotales = 0L;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String baseDate = year + "-" + month + "-" + day + " ";


        try{

            String[] tiempoSeparado = tiempo.split(":");
            /*minutosTotales += Integer.valueOf(tiempoSeparado[0]) * 60;
            minutosTotales += Integer.valueOf(tiempoSeparado[1]);*/

            baseDate += tiempoSeparado[0] + ":" + tiempoSeparado[1] + ":00";
            Date date = format.parse(baseDate);
            minutosTotales = date.getTime() - 21600000;
        } catch (Exception e){
            e.printStackTrace();
        }

        Log.e("time", String.format("Minutos: %s", String.valueOf(minutosTotales)));
        return minutosTotales;
    }












}
