package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recuperacion_password extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    EditText confirmEditText;
    EditText codeEditText;
    Button enviarCorreoBtn;
    Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_password);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmPasswordEditText);
        codeEditText = findViewById(R.id.codigoEditText);
        enviarCorreoBtn = findViewById(R.id.enviarCorreoBtn);
        resetBtn = findViewById(R.id.resetPasswordBtn);
    }

    public void ResetPassword(View view){
        // revisar que los campos de las contraseñas no esten vacíos
        String password = passwordEditText.getText().toString();
        String passwordConfirmation = confirmEditText.getText().toString();

        if(password.equals("") || passwordConfirmation.equals("")){
            Toast.makeText(this, "Campos de contraseña vaciós", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codeEditText.getText().toString().equals("")){
            Toast.makeText(this, "Por favor ingrese el código", Toast.LENGTH_SHORT).show();
        }

        if (!password.equals(passwordConfirmation)){
            Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            // reset
            JSONObject obj = new JSONObject();
            try{
                obj.put("email", emailEditText.getText().toString());
                obj.put("password", AESCrypt.encrypt(password));
                obj.put("code", codeEditText.getText().toString());

                Post_json post = new Post_json();
                DatosConsulta datos = new DatosConsulta(Post_json.RESET_PASSWORD, obj);
                JSONObject res = post.execute(datos).get();

                Log.e("recup", res.toString());

                if (res != null){
                    Log.e("url", res.toString());
                    // se verifica que no haya un error con la consulta -------------------------------------------------
                    String status = Post_json.verificarSiTieneStatus(res);
                    if (status != null){
                        String msj = res.getString("status");
                        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
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
    }

    public void enviarCorreo(View view){
        String correo = emailEditText.getText().toString();
        if (!validarEmail(correo)){
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject obj = new JSONObject();
            try{
                obj.put("email", correo);

                Post_json post = new Post_json();
                DatosConsulta datos = new DatosConsulta(Post_json.REQUEST_CODE, obj);
                JSONObject res = post.execute(datos).get();

                if (res != null){
                    Log.e("url", res.toString());
                    // se verifica que no haya un error con la consulta -------------------------------------------------
                    String status = Post_json.verificarSiTieneStatus(res);
                    if (status != null){
                        String msj = res.getString("status");
                        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
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
    }

    public boolean validarEmail(String email){

        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()){
            return true;
        }
        System.out.println(email +" : "+ matcher.matches());


        return false;
    }
}
