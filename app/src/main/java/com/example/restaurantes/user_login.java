package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class user_login extends AppCompatActivity {

    EditText emailEdittext;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailEdittext = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void ingresarConFacebook(View view){
        System.out.println("ingresar con facebook");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void validarCredenciales(View view){

        String email = emailEdittext.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!verificarCamposVacios()){
            Log.e("login",String.format("Email: %s\nPass: %s", email, password));

            JSONObject obj = new JSONObject();
            try{
                obj.put("email", email);
                obj.put("password", AESCrypt.encrypt(password));
                Log.e("pass",AESCrypt.encrypt(password));
                Log.e("pass",AESCrypt.decrypt(AESCrypt.encrypt(password)));

                Post_json post = new Post_json();
                DatosConsulta datos = new DatosConsulta(Post_json.LOGIN_USUARIO, obj);
                JSONObject res = post.execute(datos).get();

                if (res != null){
                    Log.e("url", res.toString());
                    // se verifica que no haya un error con la consulta -------------------------------------------------
                    String status = Post_json.verificarSiTieneStatus(res);
                    if (status != null){
                        Toast.makeText(this, "Datos de usuario incorrectos", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("token", res.getString("token"));
                        intent.putExtra("name", res.getString("name"));
                        intent.putExtra("email", email);
                        Log.e("login",String.format("token: %s\nname: %s", res.getString("token"), res.getString("name")));
                        startActivity(intent);

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

        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    }

    private boolean verificarCamposVacios(){
        return (emailEdittext.getText().toString().equals("") || passwordEditText.getText().toString().equals(""));
    }

    public void olvidoContrasenia(View view){
        System.out.println("olvido contraseña");
    }
}
