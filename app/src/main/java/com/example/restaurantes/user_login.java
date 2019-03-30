package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class user_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
    }

    public void ingresarConFacebook(View view){
        System.out.println("ingresar con facebook");
    }

    public void validarCredenciales(View view){
        System.out.println("validar credenciales");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void olvidoContrasenia(View view){
        System.out.println("olvido contraseña");
    }
}
