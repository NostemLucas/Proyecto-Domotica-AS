package com.lemtproyecto.casadomotica.Adaptadores;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lemtproyecto.casadomotica.R;
import com.lemtproyecto.casadomotica.modelos.Usuarios;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    private int recurso;
    private ArrayList<Usuarios> listaUsuarios;
    StorageReference storageReference;
    StorageReference imgRef;
    public AdaptadorUsuarios(ArrayList<Usuarios> listaCambios, int recurso){
        this.listaUsuarios=listaCambios;
        this.recurso=recurso;
    }

    @NonNull
    @Override
    public AdaptadorUsuarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(recurso,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuarios usuarios=listaUsuarios.get(position);
        holder.txtCorreo.setText(usuarios.getCorreo());
        holder.txtNombres.setText(usuarios.getNombre());
        holder.txtApellidos.setText(usuarios.getApellidos());
        String img = usuarios.getImagen();
        storageReference = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");
        imgRef=storageReference.child(img);
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.imgPerfil);
            }
        });
        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(v.getRootView().getContext());
                View dialogview=LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.usuarios_mensaje,null);
                TextView txtNombreU,txtApellidoU,txtCorreoU;
                ImageView imgCuentaU;
                txtNombreU=dialogview.findViewById(R.id.txtNombreU);
                txtApellidoU=dialogview.findViewById(R.id.txtApellidoU);
                txtCorreoU=dialogview.findViewById(R.id.txtCorreoU);
                imgCuentaU=dialogview.findViewById(R.id.imgCuentaU);
                txtNombreU.setText(usuarios.getNombre());
                txtApellidoU.setText(usuarios.getApellidos());
                txtCorreoU.setText(usuarios.getCorreo());
                String imgdialog = usuarios.getImagen();
                storageReference = FirebaseStorage.getInstance().getReference().child("imagenesPerfil");
                imgRef=storageReference.child(imgdialog);
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgCuentaU);
                    }
                });
                builder.setView(dialogview);
                builder.setNegativeButton("Cerrar",null);
                builder.setCancelable(true);
                builder.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCorreo, txtNombres,txtApellidos;
        private Button btnVer;
        ImageView imgPerfil;
        public View view;
        public ViewHolder(View view){
            super(view);
            this.view=view;
            this.txtCorreo=view.findViewById(R.id.txtCorreoUsuario);
            this.txtNombres=view.findViewById(R.id.txtNombreUsuario);
            this.txtApellidos=view.findViewById(R.id.txtApellidoUsuario);
            this.imgPerfil=view.findViewById(R.id.imgPerfilUsuario);
            this.btnVer=view.findViewById(R.id.btnVer);

        }
    }
}
