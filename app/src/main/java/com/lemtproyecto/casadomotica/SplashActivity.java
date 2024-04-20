package com.lemtproyecto.casadomotica;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.splash));
        }
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i=new Intent(SplashActivity.this, Bienvenida.class);
                startActivity(i);
                finish();
            }
        };thread.start();
    }
}
