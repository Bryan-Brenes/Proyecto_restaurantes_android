package com.example.restaurantes;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mixpanel = MixpanelAPI.getInstance(getApplicationContext(), LoginActivity.MIXPANEL_TOKEN);
        setContentView(R.layout.login_activity);
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
}
