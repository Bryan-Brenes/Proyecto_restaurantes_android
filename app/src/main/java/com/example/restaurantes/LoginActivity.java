package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    public void abrirPantallas(View view){
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
