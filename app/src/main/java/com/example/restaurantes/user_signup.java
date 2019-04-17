package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class user_signup extends AppCompatActivity {

    EditText nombreUsuarioEditText;
    EditText emailUsuarioEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        nombreUsuarioEditText = findViewById(R.id.nombreUsuarioEditText);
        emailUsuarioEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.passwordConfirmEditText);
    }

    public void crearCuenta(View view){
        if (!validarCamposVacios()){
            if(!validarEmail()){
                Toast.makeText(this, "Correo inválido",Toast.LENGTH_SHORT).show();
            } else {
                if (validarContrasenia()){

                    String password = passwordEditText.getText().toString();
                    try{
                        String encryptedPassword = AESCrypt.encrypt(password);
                        Log.e("pass", encryptedPassword);
                        /*System.out.println(String.format("Password: %s", password));
                        System.out.println(String.format("Encrypted password: %s", encryptedPassword));
                        System.out.println(String.format("Verificacion: %s", encryptedPassword.equals(AESCrypt.encrypt(password))));*/

                        // envio de la solicitud al backoffice
                        JSONObject obj = new JSONObject();
                        try{
                            obj.put("email", emailUsuarioEditText.getText().toString().toLowerCase());
                            obj.put("name",nombreUsuarioEditText.getText().toString().toLowerCase());
                            obj.put("password",encryptedPassword);

                            Log.e("createUser", obj.toString());

                            Post_json post = new Post_json();
                            DatosConsulta datos = new DatosConsulta(Post_json.REGISTRAR_USUARIO, obj);
                            JSONObject res = post.execute(datos).get();

                            if (res != null){
                                Log.e("url", res.toString());
                                // se verifica que no haya un error con la consulta
                                String error = Post_json.verificarSiHuboError(res);
                                if (error != null){
                                    Toast.makeText(this, "No es posible crear el usuario en este momento, trate más tarde", Toast.LENGTH_SHORT).show();
                                } else {
                                    String status = res.getString("status");
                                    if (status.equals("Successfully registered")){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("nombre", nombreUsuarioEditText.getText().toString());
                                        intent.putExtra("email", emailUsuarioEditText.getText().toString());
                                        startActivity(intent);

                                    } else if (status.equals("User is already exists")){
                                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
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

                    } catch (Exception e){
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(this, "Contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Por favor no dejar campos vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarContrasenia(){
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        return password.equals(confirmPassword);
    }

    public boolean validarEmail(){
        String email = emailUsuarioEditText.getText().toString();

        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()){
            return true;
        }
        System.out.println(email +" : "+ matcher.matches());


        return false;
    }

    // true: si hay campos vacíos
    public boolean validarCamposVacios(){
        return (nombreUsuarioEditText.getText().toString().equals("") ||
            emailUsuarioEditText.getText().toString().equals("") ||
            passwordEditText.getText().toString().equals("") ||
            confirmPasswordEditText.getText().toString().equals(""));
    }
}
