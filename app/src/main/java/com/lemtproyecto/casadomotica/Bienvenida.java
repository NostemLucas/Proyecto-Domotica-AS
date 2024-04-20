package com.lemtproyecto.casadomotica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Bienvenida extends AppCompatActivity {


    Button btnSalir, btnComenzar;
    boolean sesionado = false;


    public static String PREFERENCIAS = "preferencias";
    public static String GUARDAR_SESION = "guardar_sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.splash));
        }
        setContentView(R.layout.bienvenida);
        btnComenzar = findViewById(R.id.btnComenzar);
        btnSalir = findViewById(R.id.btnSalir);

        loadData();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;

                if (sesionado && user != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                    i = new Intent(Bienvenida.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    i = new Intent(Bienvenida.this, Login.class);
                    startActivity(i);
                    finish();
                }


            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(Bienvenida.this, Login.class);
                //startActivity(i);
                finish();
            }
        });
    }

    public void loadData() {
        SharedPreferences preferencias = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        sesionado = preferencias.getBoolean(GUARDAR_SESION, true);
    }
}