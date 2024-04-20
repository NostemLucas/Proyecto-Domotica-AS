package com.lemtproyecto.casadomotica;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.util.Date;


public class ListaUsuarios extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Usuarios> listaUsuarios = new ArrayList<>();
    private DatabaseReference mDatabase;
    EditText edtBusqueda;
    private AdaptadorUsuarios adaptadorUsuarios;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_usuarios);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.lista_usuarios));
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView=findViewById(R.id.lista_usuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        edtBusqueda=findViewById(R.id.txtBusqueda);
        recuperarUsuarios();
        edtBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            buscar(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void buscar(CharSequence s) {
        String t=String.valueOf(s);
        t=t.replace("/","-");
        ArrayList<Usuarios> listabus=new ArrayList<>();
        for(Usuarios obj:listaUsuarios){
            if (obj.getNombre().toLowerCase().contains(t.toLowerCase())) {
                listabus.add(obj);
            }
        }
        adaptadorUsuarios = new AdaptadorUsuarios(listabus,R.layout.item_usuario);
        recyclerView.setAdapter(adaptadorUsuarios);
    }

    private void recuperarUsuarios(){

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    listaUsuarios.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        String correo = ds.child("correo").getValue().toString();
                        String nombre = ds.child("nombre").getValue().toString();
                        String apellidos = ds.child("apellidos").getValue().toString();
                        String img=ds.child("imagen").getValue().toString();
                        listaUsuarios.add(new Usuarios(correo,nombre,apellidos,img));
                    }
                }
                adaptadorUsuarios = new AdaptadorUsuarios(listaUsuarios,R.layout.item_usuario);
                recyclerView.setAdapter(adaptadorUsuarios);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

