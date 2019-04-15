package com.example.restaurantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class user_signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
    }

    public void crearCuenta(View view){

        System.out.println("Crear cuenta");
    }

    public boolean validarCampos(){
        return false;
    }
}
