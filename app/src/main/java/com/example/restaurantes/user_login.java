package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class user_login extends AppCompatActivity {

    EditText emailEdittext;
    EditText passwordEditText;

    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailEdittext = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                getInfo(accessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Inicio de Sesion Cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error al conectar con facebook", Toast.LENGTH_SHORT).show();
            }
        });

        if(isLoggedIn()) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            getInfo(accessToken);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
                        /*intent.putExtra("token", res.getString("token"));
                        intent.putExtra("name", res.getString("name"));
                        intent.putExtra("email", email);*/

                        SessionManager.setToken(res.getString("token"));
                        SessionManager.setNombre(res.getString("name"));
                        SessionManager.setEmail(email);
                        Log.e("login",String.format("token: %s\nname: %s", res.getString("token"), res.getString("name")));
                        startActivity(intent);

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

        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    }

    private boolean verificarCamposVacios(){
        return (emailEdittext.getText().toString().equals("") || passwordEditText.getText().toString().equals(""));
    }

    public void olvidoContrasenia(View view){
        Intent intent = new Intent(getApplicationContext(), Recuperacion_password.class);
        startActivity(intent);
    }

    private void getInfo(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            JSONObject json = new JSONObject();
                            json.put("email", email);
                            json.put("socialLogin", true);
                            Post_json post = new Post_json();
                            DatosConsulta datos = new DatosConsulta(Post_json.LOGIN_USUARIO, json);
                            JSONObject res = post.execute(datos).get();
                            if(res != null) {
                                String status = Post_json.verificarSiTieneStatus(res);
                                if(status != null) {
                                    Toast.makeText(getApplicationContext(), "Datos de usuario incorrectos", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    SessionManager.setToken(res.getString("token"));
                                    SessionManager.setNombre(name);
                                    SessionManager.setEmail(email);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error en la respuesta de Facebook", Toast.LENGTH_SHORT).show();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }
}
