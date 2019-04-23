package com.example.restaurantes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DetallesActivity extends AppCompatActivity {

    ImageSwitcher switcher;
    Handler handler;
    ArrayList<Bitmap> images = new ArrayList<>();
    int currentIndex = 0;

    TextView nombreRestaurante;
    ImageView estrellaUno;
    ImageView estrellaDos;
    ImageView estrellaTres;
    ImageView estrellaCuarto;
    ImageView estrellaCinco;
    ImageView dineroUno;
    ImageView dineroDos;
    ImageView dineroTres;
    TextView telefono;
    LinearLayout tipoComidaVerticalLayout;
    LinearLayout horariosVerticalLayout;
    ListView comentariosListView;

    // Elementos para que el usuario ingrese un comentario
    ImageView imagenUsuarioImageView;
    TextView nombreUsuarioTextView;
    EditText comentarioUsuarioEditText;
    ImageView estrellaUnoComentarioImageView;
    ImageView estrellaDosComentarioImageView;
    ImageView estrellaTresComentarioImageView;
    ImageView estrellaCuatroComentarioImageView;
    ImageView estrellaCincoComentarioImageView;
    ImageView enviarComentarioImageView;
    ImageView dineroUnoComentarioImageView;
    ImageView dineroDosComentarioImageView;
    ImageView dineroTresComentarioImageView;
    ModeloDatoComentario comentarioUsuario;

    ModeloDatoRestaurante restauranteSeleccionado;
    ArrayList<ModeloDatoComentario> comentarios;
    CustomArrayAdapterComentarios adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        comentarios = new ArrayList<>();

        restauranteSeleccionado = RestListTabFragment.restauranteSeleccionado;
        comentarioUsuario = new ModeloDatoComentario();
        comentarioUsuario.setCalificacionNumerica(0);  // buscar si ya a calificado el restaurante
        comentarioUsuario.setPrecio(0); // Buscar si ya se ha dado un precio para el restaurante

        nombreRestaurante = findViewById(R.id.nombreRestauranteTextView);
        estrellaUno = findViewById(R.id.estrellaUnoImageView);
        estrellaDos = findViewById(R.id.estrellaDosImageView);
        estrellaTres = findViewById(R.id.estrellaTresImageView);
        estrellaCuarto = findViewById(R.id.estrellaCuatroImageView);
        estrellaCinco = findViewById(R.id.estrellaCincoImageView);
        dineroUno = findViewById(R.id.billeteUnoImageView);
        dineroDos = findViewById(R.id.billeteDosImageView);
        dineroTres = findViewById(R.id.billeteTresImageView);
        telefono = findViewById(R.id.telefonoRestTextView);
        tipoComidaVerticalLayout = findViewById(R.id.tiposComidaVerticalLayout);
        horariosVerticalLayout = findViewById(R.id.horariosVerticalLayout);
        comentariosListView = findViewById(R.id.comentariosListView);

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
                    switcher.setImageDrawable(new BitmapDrawable(images.get(currentIndex)));
                }
            }
        });

        switcher.setImageResource(R.drawable.store);

        // llenado los elementos gráficos con la informacion del restaurante
        nombreRestaurante.setText(restauranteSeleccionado.getNombre());
        setearEstrellas(restauranteSeleccionado.getCalificacionNumerica());
        setearPrecio(restauranteSeleccionado.getPrecio());

        // telefono
        int numero = restauranteSeleccionado.getNumero();
        if (numero == 0){
            telefono.setText(" - ");
        } else {
            telefono.setText(String.valueOf(numero));
        }

        //tipos de comida
        ArrayList<String> comidas = restauranteSeleccionado.getFoods();
        for (String str : comidas){
            TextView food = new TextView(this);
            food.setText(str);
            tipoComidaVerticalLayout.addView(food);
        }

        // horarios
        ArrayList<DiaHorario> horario = restauranteSeleccionado.getHorario();
        if (horario.size() == 0 || horario == null){
            TextView textView = new TextView(this);
            textView.setText(" - ");
            horariosVerticalLayout.addView(textView);
        } else {
            for (DiaHorario dia : horario){

                String lineaHorario = String.format("%1$-20s %2$10s-%3$-20s",dia.getDia(),dia.getHoraApertura().substring(11,16),dia.getHoraCierre().substring(11,16));

                TextView lineaTextView = new TextView(this);
                lineaTextView.setText(lineaHorario);
                horariosVerticalLayout.addView(lineaTextView);
            }
        }

        // Parte de comentario de usuario
        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioComentario);
        nombreUsuarioTextView.setText(SessionManager.getNombre());

        comentarioUsuarioEditText = findViewById(R.id.comentarioUsuarioEditText);
        imagenUsuarioImageView = findViewById(R.id.imagenUsuarioComentario);

        estrellaUnoComentarioImageView = findViewById(R.id.estrellaUnoComentarioImageView);
        estrellaDosComentarioImageView = findViewById(R.id.estrellaDosComentarioImageView);
        estrellaTresComentarioImageView = findViewById(R.id.estrellaTresComentarioImageView);
        estrellaCuatroComentarioImageView = findViewById(R.id.estrellaCuatroComentarioImageView);
        estrellaCincoComentarioImageView = findViewById(R.id.estrellaCincoComentarioImageView);
        enviarComentarioImageView = findViewById(R.id.sendImageView);

        dineroUnoComentarioImageView = findViewById(R.id.billeteUnoComentarioImageView);
        dineroDosComentarioImageView = findViewById(R.id.billeteDosComentarioImageView);
        dineroTresComentarioImageView = findViewById(R.id.billeteTresComentarioImageView);

        comentarios = obtenerComentarios();
        adapter = new CustomArrayAdapterComentarios(getApplicationContext(), comentarios);
        comentariosListView.setAdapter(adapter);
        comentariosListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        obtainPhotos();

    }

    private void obtainPhotos() {
        images.clear();
        JsonObject json = new JsonObject();
        json.addProperty("email", SessionManager.getEmail());
        json.addProperty("id", restauranteSeleccionado.getIdRestaurante());
        json.addProperty("token", SessionManager.getToken());
        Future uploading = Ion.with(DetallesActivity.this)
                .load("POST", Post_json.OBTENER_FOTOS)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        ArrayList<JsonArray> photos = new ArrayList<>();
                        JsonArray array = result.getAsJsonArray("photos");
                        JsonElement element = result.get("token");
                        String token = element.getAsString();
                        SessionManager.setToken(token);
                        for(int i = 0; i < array.size(); i++) {
                            JsonElement value = array.get(i);
                            JsonObject content = value.getAsJsonObject();
                            JsonObject photo = content.getAsJsonObject("photo");
                            JsonArray data = photo.getAsJsonArray("data");
                            photos.add(data);
                        }
                        if(photos.size() > 0) {
                            Process process = new Process(photos);
                            try {
                                images = process.execute().get();
                            }
                            catch (ExecutionException e1) {
                                e1.printStackTrace();
                            }
                            catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void setearEstrellas(int calificacion){
        switch (calificacion){
            case 0:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                break;
            case 1:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                break;
            case 2:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                break;
            case 3:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                break;
            case 4:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                break;
            case 5:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                break;
            default:
                estrellaUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuarto.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCinco.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
        }
    }

    private void setearPrecio(int precio){
        dineroUno.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
        dineroDos.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
        dineroTres.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
        switch (precio){
            case 0:
                dineroUno.setVisibility(View.INVISIBLE);
                dineroDos.setVisibility(View.INVISIBLE);
                dineroTres.setVisibility(View.INVISIBLE);
                break;
            case 1:
                dineroUno.setVisibility(View.VISIBLE);
                dineroDos.setVisibility(View.INVISIBLE);
                dineroTres.setVisibility(View.INVISIBLE);
                break;
            case 2:
                dineroUno.setVisibility(View.VISIBLE);
                dineroDos.setVisibility(View.VISIBLE);
                dineroTres.setVisibility(View.INVISIBLE);
                break;
            case 3:
                dineroUno.setVisibility(View.VISIBLE);
                dineroDos.setVisibility(View.VISIBLE);
                dineroTres.setVisibility(View.VISIBLE);
                break;
            default:
                dineroUno.setVisibility(View.INVISIBLE);
                dineroDos.setVisibility(View.INVISIBLE);
                dineroTres.setVisibility(View.INVISIBLE);

        }
    }

    public void enviarComentario(View view){
        String comentario = comentarioUsuarioEditText.getText().toString();
        if(comentarioUsuario.getCalificacionNumerica() == 0) {
            Toast.makeText(this, "No ha calificado el restaurante", Toast.LENGTH_SHORT).show();
        } else if(comentarioUsuario.getPrecio() == 0){
            Toast.makeText(this, "No ha calificado el precio del restaurante", Toast.LENGTH_SHORT).show();
        } else {
            // enviar comentario
            JSONObject obj = new JSONObject();
            try{
                obj.put("token", SessionManager.getToken());
                obj.put("email", SessionManager.getEmail());
                obj.put("id", restauranteSeleccionado.getIdRestaurante());

                JSONObject opnion = new JSONObject();
                opnion.put("calification", comentarioUsuario.getCalificacionNumerica());
                opnion.put("price", comentarioUsuario.getPrecio());
                opnion.put("date", System.currentTimeMillis() - 21600000);
                if (!comentarioUsuarioEditText.getText().toString().equals("")){
                    opnion.put("comment", comentarioUsuarioEditText.getText().toString());
                }

                obj.put("opinion", opnion);

                Log.e("opiniones", obj.toString());

                Post_json post = new Post_json();
                DatosConsulta datos = new DatosConsulta(Post_json.CREAR_OPINION, obj);
                JSONObject res = post.execute(datos).get();

                if (res != null){
                    Log.e("opiniones", res.toString());
                    // se verifica que no haya un error con la consulta -------------------------------------------------
                    String status = Post_json.verificarSiTieneStatus(res);
                    if (!status.equals("Opinion added")){
                        Toast.makeText(this, "No se pudo agregar la opinion", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Opinión agregada", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

                    }
                } else {
                    Log.e("url", "respuesta nula");
                    Toast.makeText(this, "No se puede ingresar en este momento", Toast.LENGTH_SHORT).show();
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
    }

    public void actualizarEstrellasCalificacionUsuario(View view){
        ImageView estrella = (ImageView) view;
        Log.e("opiniones", estrella.getTag().toString());

        switch (estrella.getTag().toString()){
            case "estrellaUnoComentario":
                estrellaUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuatroComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCincoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                comentarioUsuario.setCalificacionNumerica(1);
                break;
            case "estrellaDosComentario":
                estrellaUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCuatroComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCincoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                comentarioUsuario.setCalificacionNumerica(2);
                break;
            case "estrellaTresComentario":
                estrellaUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuatroComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                estrellaCincoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                comentarioUsuario.setCalificacionNumerica(3);
                break;
            case "estrellaCuatroComentario":
                estrellaUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuatroComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCincoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_hollow));
                comentarioUsuario.setCalificacionNumerica(4);
                break;
            case "estrellaCincoComentario":
                estrellaUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCuatroComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                estrellaCincoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_filled));
                comentarioUsuario.setCalificacionNumerica(5);
                break;
        }
    }

    public void actualizarDineroCalificacionUsuario(View view){
        ImageView estrella = (ImageView) view;
        Log.e("opiniones", estrella.getTag().toString());

        switch (estrella.getTag().toString()){
            case "billeteUnoComentario":
                dineroUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                dineroDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change_hollow));
                dineroTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change_hollow));
                comentarioUsuario.setPrecio(1);
                break;
            case "billeteDosComentario":
                dineroUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                dineroDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                dineroTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change_hollow));
                comentarioUsuario.setPrecio(2);
                break;
            case "billeteTresComentario":
                dineroUnoComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                dineroDosComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                dineroTresComentarioImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.change));
                comentarioUsuario.setPrecio(3);
                break;
        }
    }

    private ArrayList<ModeloDatoComentario> obtenerComentarios(){
        ArrayList<ModeloDatoComentario> comentariosArrayList = new ArrayList<>();

        JSONObject obj = new JSONObject();
        try{
            obj.put("token", SessionManager.getToken());
            obj.put("email", SessionManager.getEmail());
            obj.put("id", restauranteSeleccionado.getIdRestaurante());
            Log.e("opinions", obj.toString());

            Post_json post = new Post_json();
            DatosConsulta datos = new DatosConsulta(Post_json.OBTENER_OPINIONES, obj);
            JSONObject res = post.execute(datos).get();

            if (res != null){
                Log.e("opiniones", res.toString());
                // se verifica que no haya un error con la consulta -------------------------------------------------
                String status = Post_json.verificarSiTieneStatus(res);
                if (status.equals("Successfull")){
                    // se actualiza el token
                    String token = res.getString("token");
                    SessionManager.setToken(token);

                    // Falta por implementar despues de agregar un comentario
                    JSONArray opinionsJsonArray = res.getJSONArray("opinions");
                    if (opinionsJsonArray.length() != 0){
                        for (int i = 0; i < opinionsJsonArray.length(); i++){
                            JSONObject opinion = (JSONObject) opinionsJsonArray.get(i);
                            int calification = opinion.getInt("calification");
                            int price = opinion.getInt("price");
                            String date = opinion.getString("date");
                            String comment = opinion.getString("comment");
                            String user = opinion.getString("user");
                            String[] userData = user.split("@");

                            ModeloDatoComentario com = new ModeloDatoComentario(this, null, userData[0], null, calification, price,date);

                            if(comment.equals("null")){
                                com.setComentario(null);
                            } else {
                                com.setComentario(comment);
                            }
                            comentariosArrayList.add(com);
                        }
                    }
                } else {
                    Toast.makeText(this, "No se pueden obtener las opiniones en este momento", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("url", "respuesta nula");
                Toast.makeText(this, "No se pueden obtener las opiniones en este momento", Toast.LENGTH_SHORT).show();
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

        return comentariosArrayList;
    }

}
