package com.lemtproyecto.casadomotica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DatabaseReference databaseReference,databaseReferenceS,
            databaseReferenceE,databaseReferenceC,
            databaseReferenceHum,databaseReferenceTemp,databaseReferenceVen;
    Button mtbDormitorio, mtbCocina, mtbEscaleras,mbtSalon,mbtVentilador;
    LottieAnimationView luzDormitorio,luzSalon,luzEscaleras,luzCocina,ventilador, info;
    Boolean lzd= false,lzs= false,lze=false,lzc=false,ven=false;
    TextView txtTemperatura,txtHumedad,txtUsuario;
    ImageView imgPerfil;
    DatabaseReference dbreference,refAcciones;
    StorageReference imgRef;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    String uid;
    //botones de Interfaz
    ImageButton btnRegistroCambios,btnOpciones, btnCerrarSesion,btnUsuarios, btnMic;
    //Para el microfono
    private static final int REQ_CODE_SPEECH_INPUT=100;
    //Para el sonido
    MediaPlayer mp;
    Boolean sonidos=false;
    ProgressDialog pd;
    public static String PREFERENCIAS = "preferencias";
    public static String GUARDAR_MUSICA = "guardar_musica";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.teal_700));
        }
        loadData();
        pd = new ProgressDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        pd.setTitle("Cargando Información");
        pd.setMessage("Esto tomara unos instantes");
        pd.setCancelable(false);
        pd.show();

        mp= MediaPlayer.create(this,R.raw.sonido1);

        txtHumedad=findViewById(R.id.txtHumedad);
        txtTemperatura=findViewById(R.id.txtTemperatura);
        txtUsuario=findViewById(R.id.txtUsuarioActivo);
        imgPerfil=findViewById(R.id.imgPerfil);
        btnCerrarSesion=findViewById(R.id.btnCerrarSesion);
        btnOpciones=findViewById(R.id.btnOpciones);
        btnUsuarios=findViewById(R.id.btnUsuarios);
        btnMic=findViewById(R.id.btnMicrofono);
        btnRegistroCambios=findViewById(R.id.btnRegistraCambios);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                FirebaseAuth.getInstance().signOut();
                Intent i= new Intent(MainActivity.this, Bienvenida.class);
                startActivity(i);
                finish();

            }
        });
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                Intent i = new Intent(MainActivity.this, Ajustes.class);
                startActivity(i);
            }
        });
        btnRegistroCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                Intent i = new Intent(MainActivity.this, ListaCambios.class);
                startActivity(i);
            }
        });
        btnUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                Intent i = new Intent(MainActivity.this, ListaUsuarios.class);
                startActivity(i);
            }
        });

        //--------------------------Dormitorio
        mtbDormitorio=findViewById(R.id.btnDormitorio);
        luzDormitorio=findViewById(R.id.luzDormitorio);
        //--------------------------Salon
        mbtSalon=findViewById(R.id.btnSalon);
        luzSalon=findViewById(R.id.luzSalon);
        //--------------------------Escaleras
        mtbEscaleras=findViewById(R.id.btnEscalera);
        luzEscaleras=findViewById(R.id.luzEscalera);
        //--------------------------Cocina
        mtbCocina=findViewById(R.id.btnCocina);
        luzCocina=findViewById(R.id.luzCocina);
        //--------------------------Ventilador
        mbtVentilador=findViewById(R.id.btnVentilador);
        ventilador=findViewById(R.id.anmVentilador);
        //--------------------------Conexion

        FirebaseDatabase database = FirebaseDatabase.getInstance();
         databaseReference = database.getReference("led_dormitorio");
         databaseReferenceS = database.getReference("led_salon");
         databaseReferenceE = database.getReference("led_gradas");
         databaseReferenceC = database.getReference("led_cocina");
         databaseReferenceHum=database.getReference("humedad");
         databaseReferenceTemp=database.getReference("temperatura");
         databaseReferenceVen=database.getReference("ventilacion");
        //Mostramos el usuario activo y su imagen
         dbreference = FirebaseDatabase.getInstance().getReference("users");
         firebaseAuth = FirebaseAuth.getInstance();

         uid = firebaseAuth.getCurrentUser().getUid();
         dbreference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre=snapshot.child("nombre").getValue(String.class);

                    txtUsuario.setText(nombre);
                    String img = snapshot.child("imagen").getValue(String.class);
                    storageReference = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");
                    imgRef=storageReference.child(img);
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                      public void onSuccess(Uri uri) {

                          Picasso.get().load(uri).into(imgPerfil);
                          pd.dismiss();
                        }
                   }
                );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //Evento on addValueListener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if ( value != null) {
                    if (value == 1) {
                        luzDormitorio.setMinAndMaxProgress(0.0f,0.30f);
                        luzDormitorio.setSpeed(1);
                        luzDormitorio.playAnimation();
                        lzd=true;
                        mtbDormitorio.setText("Apagar");
                    }
                    else{
                        luzDormitorio.setMinAndMaxProgress(0.0f,0.25f);
                        luzDormitorio.setSpeed(-1);
                        luzDormitorio.playAnimation();
                        lzd=false;
                        mtbDormitorio.setText("Encender");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        databaseReferenceS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if ( value != null) {
                    if (value == 1) {
                        luzSalon.setMinAndMaxProgress(0.0f,0.30f);
                        luzSalon.setSpeed(1);
                        luzSalon.playAnimation();
                        lzs=true;
                        mbtSalon.setText("Apagar");
                    }
                    else{
                        luzSalon.setMinAndMaxProgress(0.0f,0.25f);
                        luzSalon.setSpeed(-1);
                        luzSalon.playAnimation();
                        lzs=false;
                        mbtSalon.setText("Encender");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        databaseReferenceE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if ( value != null) {
                    if (value == 1) {
                        luzEscaleras.setMinAndMaxProgress(0.0f,0.30f);
                        luzEscaleras.setSpeed(1);
                        luzEscaleras.playAnimation();
                        lze=true;
                        mtbEscaleras.setText("Apagar");
                    }
                    else{
                        luzEscaleras.setMinAndMaxProgress(0.0f,0.25f);
                        luzEscaleras.setSpeed(-1);
                        luzEscaleras.playAnimation();
                        lze=false;
                        mtbEscaleras.setText("Encender");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        databaseReferenceC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if ( value != null) {
                    if (value == 1) {
                        luzCocina.setMinAndMaxProgress(0.0f,0.30f);
                        luzCocina.setSpeed(1);
                        luzCocina.playAnimation();
                        lzc=true;
                        mtbCocina.setText("Apagar");
                    }
                    else{
                        luzCocina.setMinAndMaxProgress(0.0f,0.25f);
                        luzCocina.setSpeed(-1);
                        luzCocina.playAnimation();
                        lzc=false;
                        mtbCocina.setText("Encender");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        //Para la humedad y la temperatura
        databaseReferenceHum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if ( value != null) {
                        txtHumedad.setText(value + "%");
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        databaseReferenceTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if ( value != null) {
                    txtTemperatura.setText(value + " C");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        //Para la ventilacion
        databaseReferenceVen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if ( value != null) {
                    if (value == 1) {
                        ventilador.setSpeed(1);
                        ventilador.playAnimation();
                        ventilador.loop(true);
                        ven=true;
                        mbtVentilador.setText("Apagar");
                    }
                    else{
                        ventilador.setSpeed(0);
                        ventilador.playAnimation();
                        ven=false;
                        mbtVentilador.setText("Encender");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        //---------------------------Eventos OnCLick
        mtbDormitorio.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if (sonidos){
                 mp.start();
             }
             lzd=cambiar_estado(lzd,databaseReference);
             registroAcciones("Luz de Dormitorio");

         }
     });
        mbtSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                lzs=cambiar_estado(lzs,databaseReferenceS);
                registroAcciones("Luz de Salon");

            }
        });
        mtbEscaleras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                lze=cambiar_estado(lze,databaseReferenceE);
                registroAcciones("Luz de Escaleras");
            }
        });
        mtbCocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                lzc=cambiar_estado(lzc,databaseReferenceC);
                registroAcciones("Luz de Cocina");
            }
        });
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                Intent i=new Intent(MainActivity.this,Perfil.class);
                startActivity(i);
                finish();
            }
        });
        mbtVentilador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                ven=cambiar_estado(ven,databaseReferenceVen);
                registroAcciones("La ventilación");
            }
        });
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonidos){
                    mp.start();
                }
                iniciarEntradaVoz();
            }
        });
     }

    public boolean cambiar_estado(Boolean estado, DatabaseReference dr){
        if (estado){
            estado= false;
            dr.setValue(0);
        }
        else{
            estado=true;
            dr.setValue(1);
        }
        //Toast.makeText(getBaseContext(), String.valueOf(estado), Toast.LENGTH_SHORT).show();
        return estado;
    }




    public void registroAcciones(String cambio){
    refAcciones= FirebaseDatabase.getInstance().getReference();
        if(!txtUsuario.getText().toString().equals("Usuario")){
            Map<String,Object> mapA= new HashMap<>();
            mapA.put("correo",txtUsuario.getText().toString());
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss" );
            String hora_fecha = sdf.format(new Date());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy" );
            String dia = simpleDateFormat.format(new Date());
            mapA.put("modificacion",hora_fecha);
            mapA.put("cambio",cambio);
            refAcciones.child("Acciones").child(dia).push().setValue(mapA);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQ_CODE_SPEECH_INPUT:{
                if(resultCode==RESULT_OK && null!=data){
                    ArrayList<String> resultado=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getBaseContext(), resultado.get(0), Toast.LENGTH_SHORT).show();
                    String instruccion=resultado.get(0).toLowerCase();
                    switch (instruccion){
                        case "encender luz de dormitorio":
                            lzd=cambiar_estado(lzd,databaseReference);
                            registroAcciones("Luz de Dormitorio");
                            break;
                        case "apagar luz de dormitorio":
                            lzd=cambiar_estado(lzd,databaseReference);
                            registroAcciones("Luz de Dormitorio");
                            break;
                        case "encender luz de salón":
                            lzs=cambiar_estado(lzs,databaseReferenceS);
                            registroAcciones("Luz de Salon");
                            break;
                        case "apagar luz de salón":
                            lzs=cambiar_estado(lzs,databaseReferenceS);
                            registroAcciones("Luz de Salon");
                            break;
                        case "encender luz de cocina":
                            lzc=cambiar_estado(lzc,databaseReferenceC);
                            registroAcciones("Luz de Cocina");
                            break;
                        case "apagar luz de cocina":
                            lzc=cambiar_estado(lzc,databaseReferenceC);
                            registroAcciones("Luz de Cocina");
                            break;
                        case "encender luz de escalera":
                            lze=cambiar_estado(lze,databaseReferenceE);
                            registroAcciones("Luz de Escaleras");
                            break;
                        case "apagar luz de escalera":
                            lze=cambiar_estado(lze,databaseReferenceE);
                            registroAcciones("Luz de Escaleras");
                            break;

                        case "encender ventilación":
                            ven=cambiar_estado(ven,databaseReferenceVen);
                            registroAcciones("La ventilación");
                            break;
                        case "apagar ventilación":
                            ven=cambiar_estado(ven,databaseReferenceVen);
                            registroAcciones("La ventilación");
                            break;
                    }
                }
                break;
            }
        }
    }

    private void iniciarEntradaVoz(){
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Dime una instruccion");
        try{
            startActivityForResult(i,REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void loadData() {
        SharedPreferences preferencias = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        sonidos = preferencias.getBoolean(GUARDAR_MUSICA, true);
        Toast.makeText(this,String.valueOf(sonidos),Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

