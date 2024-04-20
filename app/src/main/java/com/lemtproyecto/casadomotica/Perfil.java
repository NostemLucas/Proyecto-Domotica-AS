package com.lemtproyecto.casadomotica;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity
{
    ImageView imgPerfil;
    EditText txtnombre,txtapellidos;
    TextView txtcorreo,txtRecuperar;
    Button btnActualizar,btnSalir;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbreference;
    StorageReference storageReference;
    String uid;
    ProgressDialog pd;
    Uri imagenUri;
    StorageReference imgRef;
    ProgressDialog pd1;
    Button btnGaleria,btnCamara;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        pd1 = new ProgressDialog(Perfil.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        pd1.setTitle("Cargando Información");
        pd1.setMessage("Esto tomara unos instantes");
        pd1.setCancelable(false);
        pd1.show();
        btnCamara=findViewById(R.id.btnCamara);
        btnGaleria=findViewById(R.id.btnGaleria);
        imgPerfil=findViewById(R.id.imgCuenta);
        btnActualizar=findViewById(R.id.btnActualisar);
        btnSalir=findViewById(R.id.btnRegresar);
        txtnombre=findViewById(R.id.txtNombre);
        txtapellidos=findViewById(R.id.txtApellido);
        txtcorreo=findViewById(R.id.txtCorreoCuenta);
        txtRecuperar=findViewById(R.id.txtRecuperarPassword);
       //Conectamos con Firebase
        dbreference= FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth=FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        //Cargamos los datos
        dbreference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String correo=snapshot.child("correo").getValue(String.class);
                    txtcorreo.setText(correo);
                    String nombre=snapshot.child("nombre").getValue(String.class);
                    txtnombre.setText(nombre);
                    String apellidos=snapshot.child("apellidos").getValue(String.class);
                    txtapellidos.setText(apellidos);
                    String img= snapshot.child("imagen").getValue(String.class);
                    imgRef=storageReference.child(img);
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imgPerfil);
                            pd1.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Perfil.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
    btnSalir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    });
    txtRecuperar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebaseAuth.setLanguageCode("es");
            firebaseAuth.sendPasswordResetEmail(txtcorreo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Perfil.this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Perfil.this, "No se pudo encontrar la direccion especificada", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });
    btnActualizar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pd = new ProgressDialog(Perfil.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
            pd.setTitle("Actualizando Perfil");
            pd.setMessage("Esto tomara unos instantes");
            pd.setCancelable(false);
            pd.show();
            if(imagenUri!=null){
                subirImagenFirebase(imagenUri);
            }
            else{
                pd.dismiss();
            }
            Map<String,Object> mapaPerfil= new HashMap<>();
            mapaPerfil.put("correo",txtcorreo.getText().toString());
            mapaPerfil.put("nombre",txtnombre.getText().toString());
            mapaPerfil.put("apellidos",txtapellidos.getText().toString());
            mapaPerfil.put("imagen",uid+".jpg");
            dbreference.child(uid).updateChildren(mapaPerfil);


        }
    });
    //Para la imagen
       storageReference= FirebaseStorage.getInstance().getReference().child("imagenesPerfil");
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
        Intent abrirGaleria= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(abrirGaleria,1000);
        }
    });
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirCamara= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(abrirCamara,101);
            }
        });
        txtcorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Perfil.this, "El campo de correo no se puede esitar debido a que es su identificador para usar la apliacion", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                imagenUri = data.getData();
                imgPerfil.setImageURI(imagenUri);
                 }
        }
        else if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK) {
                capturarImagen(data);
            }
        }
    }
    public void capturarImagen(Intent data){
        Bitmap thumbnail =(Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[]=bytes.toByteArray();
        imgPerfil.setImageBitmap(thumbnail);
        subirImagenFirebaseCamara(bb);
    }
    private void subirImagenFirebase(Uri imagenUri) {
      imgRef=storageReference.child(uid+".jpg");
      imgRef.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                      pd.dismiss();
                  }
              });
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            pd.dismiss();
          }
      });
    }
    private void subirImagenFirebaseCamara(byte[] bb) {
        imgRef=storageReference.child(uid+".jpg");
        imgRef.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}