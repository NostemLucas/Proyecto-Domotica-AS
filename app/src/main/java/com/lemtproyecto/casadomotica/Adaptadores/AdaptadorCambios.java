package com.lemtproyecto.casadomotica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lemtproyecto.casadomotica.R;
import com.lemtproyecto.casadomotica.modelos.Cambios;

import java.util.ArrayList;

public class AdaptadorCambios extends RecyclerView.Adapter<AdaptadorCambios.ViewHolder> {

    private int recurso;
    private ArrayList<Cambios> listaCambios;
    public AdaptadorCambios(ArrayList<Cambios> listaCambios,int recurso){
    this.listaCambios=listaCambios;
    this.recurso=recurso;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(recurso,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Cambios cambios=listaCambios.get(position);
    holder.txtCorreo.setText(cambios.getCorreo());
    holder.txtModificacion.setText(cambios.getModificacion());
    holder.txtFecha.setText(cambios.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaCambios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCorreo, txtFecha,txtModificacion;
        public View view;
        public ViewHolder(View view){
            super(view);
            this.view=view;
            this.txtCorreo=view.findViewById(R.id.txtCooreoCambio);
            this.txtModificacion=view.findViewById(R.id.txtCambio);
            this.txtFecha=view.findViewById(R.id.txtFechaCambio);

        }
    }
}
