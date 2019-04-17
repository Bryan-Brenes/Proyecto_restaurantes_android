package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NuevoRestauranteActivity extends AppCompatActivity {

    private String token;
    private String email;
    private String nombre;
    private String accion;
    private String id;

    ImageView imagenRestauranteImageView;
    ImageButton editImageButton;
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
    EditText vienesCierraEditText;
    EditText sabadoAbreEditText;
    EditText sabadoCierraEditText;
    EditText domingoAbreEditText;
    EditText domingoCierraEditText;
    LinearLayout tipoComidaLinearLayout;

    Button agregarRestauranteBtn;

    ArrayList<String> tiposComida;
    ArrayList<CheckBox> checkBoxesTiposComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_restaurante);

        token = getIntent().getStringExtra("token");
        nombre = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        accion = getIntent().getStringExtra("accion");

        id = null;
        if (accion.equals("actualizar")){
            id = getIntent().getStringExtra("id");
        }

        agregarRestauranteBtn = findViewById(R.id.agregarBtn);
        editImageButton = findViewById(R.id.editImageButton);
        imagenRestauranteImageView = findViewById(R.id.imagenRestauranteImageView);
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
        vienesCierraEditText = findViewById(R.id.viernesCierraEditText);
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
    }

    private void habilitarEdicion(String action){
        // deshabilitar edicion
        if (action.equals("nuevo") || action.equals("actualizar")){
            imagenRestauranteImageView.setEnabled(true);
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
            vienesCierraEditText.setEnabled(true);
            sabadoAbreEditText.setEnabled(true);
            sabadoCierraEditText.setEnabled(true);
            domingoAbreEditText.setEnabled(true);
            domingoCierraEditText.setEnabled(true);
            for(CheckBox checkBox: checkBoxesTiposComida){
                checkBox.setEnabled(true);
            }
        } else {
            imagenRestauranteImageView.setEnabled(false);
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
            vienesCierraEditText.setEnabled(false);
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
            editImageButton.setVisibility(View.INVISIBLE);
        } else if(action.equals("actualizar")){
            agregarRestauranteBtn.setText("Actualizar");
            agregarRestauranteBtn.setVisibility(View.VISIBLE);
            editImageButton.setVisibility(View.VISIBLE);
        } else{
            agregarRestauranteBtn.setVisibility(View.INVISIBLE);
            editImageButton.setVisibility(View.VISIBLE);
        }

    }

    private void obtenerTiposComida(){

        // se realiza la solicitud para obtener los tipos de comida
        JSONObject obj = new JSONObject();
        try{

            obj.put("token", token);
            obj.put("email", email);

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
            // agregar el restaurante a la base de datos
            
        } else{
            // modificar el restaurante
        }
    }

    public void agregarFoto(View view){

    }

    public void accionEditImageButton(View view){
        habilitarEdicion("actualizar");
    }

    private void poblarFormulario(){

    }
}
