package com.lemtproyecto.casadomotica;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lemtproyecto.casadomotica.Adaptadores.AdaptadorCambios;
import com.lemtproyecto.casadomotica.Adaptadores.AdaptadorUsuarios;
import com.lemtproyecto.casadomotica.modelos.Cambios;
import com.lemtproyecto.casadomotica.modelos.Usuarios;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;


public class ListaCambios extends AppCompatActivity {
    RecyclerView recyclerView;
    Button btnHoy,btnInvertir;
    ImageButton imgBuscar;
    ArrayList<Cambios> listaCambios = new ArrayList<>();
    private DatabaseReference mDatabase;
    EditText edtAño,edtDia,edtmes;
    private AdaptadorCambios adaptadorCambios;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_cambios);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.usuarios));
        }

        imgBuscar=findViewById(R.id.imgBuscar);
        btnHoy=findViewById(R.id.btnHoy);
        btnInvertir=findViewById(R.id.btnInvertir);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView=findViewById(R.id.lista_cambios);
        edtDia=findViewById(R.id.txtdia);
        edtmes=findViewById(R.id.txtMes);
        edtAño=findViewById(R.id.txtAño);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recuperarCambios();
        imgBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dia=edtDia.getText().toString();
                String mes=edtmes.getText().toString();
                String año=edtAño.getText().toString();
                if(!dia.isEmpty() && !mes.isEmpty() && !año.isEmpty()){
                    recuperarCambiosDia(dia+"/"+mes+"/"+año);
                }
                else{
                    Toast.makeText(ListaCambios.this,"Los campos deben estar llenos",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarCambios();
            }
        });
        btnInvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invertir();
            }
        });
    }
private void recuperarCambios(){
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy" );
    String dia = simpleDateFormat.format(new Date());
    mDatabase.child("Acciones").child(dia).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                listaCambios.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String correo = ds.child("correo").getValue().toString();
                    String cambio = ds.child("cambio").getValue().toString();
                    String fecha = ds.child("modificacion").getValue().toString();
                    listaCambios.add(new Cambios(fecha,correo,cambio));
                }
            }
            adaptadorCambios = new AdaptadorCambios(listaCambios,R.layout.item);
            recyclerView.setAdapter(adaptadorCambios);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    private void recuperarCambiosDia(String dia){
        dia=dia.replace("/","-");
        mDatabase.child("Acciones").child(dia).addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    listaCambios.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        String correo = ds.child("correo").getValue().toString();
                        String cambio = ds.child("cambio").getValue().toString();
                        String fecha = ds.child("modificacion").getValue().toString();
                        listaCambios.add(new Cambios(fecha,correo,cambio));
                    }
                }else{
                    listaCambios.clear();
                    Toast.makeText(ListaCambios.this,"No existen registros en la fecha proporcionada",Toast.LENGTH_SHORT).show();

                }
                adaptadorCambios = new AdaptadorCambios(listaCambios,R.layout.item);
                recyclerView.setAdapter(adaptadorCambios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void invertir(){
        Collections.reverse(listaCambios);
        adaptadorCambios.notifyDataSetChanged();
    }
}

