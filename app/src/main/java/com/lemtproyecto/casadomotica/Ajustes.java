package com.lemtproyecto.casadomotica;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class Ajustes extends AppCompatActivity {
    LottieAnimationView sw1,sw4;
    Boolean gs;
    Boolean gm;
    Button btnGuardar;
    Button btnComandos;

    public static String PREFERENCIAS = "preferencias";
    public static String GUARDAR_SESION = "guardar_sesion";
    public static String GUARDAR_MUSICA = "guardar_musica";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.lista_usuarios));
        }
        sw1=findViewById(R.id.switch1);
        sw4=findViewById(R.id.switch4);
        btnGuardar=findViewById(R.id.btnGuardaConfig);
        btnComandos=findViewById(R.id.btnComandos);
        btnComandos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarComandos();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                finish();
            }
        });
        sw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gs= cambiar_estado(sw1,gs);

            }
        });

        sw4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gm= cambiar_estado(sw4,gm);
            }
        });
        loadData();
    }
public boolean cambiar_estado( LottieAnimationView swit, Boolean isOn){
    if (isOn){
        swit.setMinAndMaxProgress(0.5f,1.0f);
        swit.playAnimation();
        isOn= false;
    }
    else{
        swit.setMinAndMaxProgress(0.0f,0.5f);
        swit.playAnimation();
        isOn=true;
    }
    Toast.makeText(getBaseContext(), String.valueOf(isOn), Toast.LENGTH_SHORT).show();
    return isOn;
}
        public void cargar_estado(LottieAnimationView swit, Boolean isOn){
        if (isOn){
            swit.setMinAndMaxProgress(0.0f,0.5f);
            swit.playAnimation();
        }
        }

    public void saveData(){
        SharedPreferences sharedPreferences= getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean(GUARDAR_SESION,gs);
        editor.putBoolean(GUARDAR_MUSICA,gm);
        editor.commit();
    }
    public void loadData(){
        SharedPreferences preferencias= getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        gs = preferencias.getBoolean(GUARDAR_SESION, true);
        gm = preferencias.getBoolean(GUARDAR_MUSICA, true);
        cargar_estado(sw1,gs);
        cargar_estado(sw4,gm);
    }
    public void mostrarComandos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Ajustes.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.comandos, null);
        builder.setView(dialogview);
        builder.setCancelable(false);
        builder.setNegativeButton("Cerrar",null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}