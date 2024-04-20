package com.lemtproyecto.casadomotica;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lemtproyecto.casadomotica.modelos.Usuarios;

import java.util.regex.Pattern;

public class Registro extends Activity {

    TextInputLayout txtCorreo,txtPassword,txtPassword2,txtNombre,txtApellido;
    TextView txtIngresar;
    Button btnRegister;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ConnectivityManager connectivityManager;
    boolean isConnected=false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        inicializar();
        registerNetworkCallback();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        txtIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();}
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isConnected){
                    showDialog();
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(Registro.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    pd.setTitle("Creando Cuenta");
                    pd.setMessage("Esto tomara unos instantes");
                    pd.show();
                    String correo =txtCorreo.getEditText().getText().toString();
                    String password=txtPassword.getEditText().getText().toString();
                    String password2=txtPassword2.getEditText().getText().toString();
                    String nombres=txtNombre.getEditText().getText().toString();
                    String apellidos=txtApellido.getEditText().getText().toString();
                    boolean a =  esCorreoValido(correo);
                    boolean b =  esPaswordValido(password);
                    boolean c = esIgual(password,password2);
                    boolean d = verificarUsuario(nombres);
                    boolean e = verificarApellidos(apellidos);

                    if ( a && b && c && d && e){
                        firebaseAuth.createUserWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(Registro.this, "Se envio un correo de verificacion al correo proporcionado", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    Usuarios usuarios =new Usuarios(correo,nombres,apellidos,"user.png");
                                    databaseReference.child(uid).setValue(usuarios);
                                    Toast.makeText(Registro.this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                    finish();
                                }else{
                                    String errorCode=((FirebaseAuthException) task.getException()).getErrorCode();
                                    Toastdeerror(errorCode);
                                    pd.dismiss();
                                }
                            }
                        });
                    }else{
                        pd.dismiss();
                    }
                }
            }
        });
    }

    private void inicializar() {
        txtCorreo=findViewById(R.id.txtCorreo);
        txtPassword=findViewById(R.id.txtPassword);
        txtPassword2=findViewById(R.id.txtPasword2);
        txtNombre=findViewById(R.id.txtNombres);
        txtApellido=findViewById(R.id.txtApellidos);
        txtIngresar=findViewById(R.id.txtLogin);
        btnRegister=findViewById(R.id.btnRegister);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.noche));
        }
    }


    private boolean esIgual(String password2, String password) {
        if (password2.isEmpty()){
            txtPassword2.setError("Este campo no puede estar vacio");
            return false;
        }
        else if(!password.equals(password2)){
            txtPassword2.setError("Las contraseñas no coinciden");
            return false;
        }
        else {
            txtPassword2.setError(null);
        }
        return true;
    }



    private boolean esCorreoValido(String correo) {

        if (correo.isEmpty()){
            txtCorreo.setError("Este campo no puede estar vacio");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches() ) {
            txtCorreo.setError("Correo electrónico inválido");
            return false;
        }
        else {
            txtCorreo.setError(null);
        }
        return true;
    }
    private boolean esPaswordValido(String password) {
        Pattern passwordRegex = Pattern.compile(
                "^"+
                        "(?=.*[0-9])"+
                        "(?=.*[a-z])"+
                        "(?=.*[A-Z])"+
                        "(?=.*[@#$%^&+=])"+
                        "(?=\\S+$)"+
                        ".{6,}"+
                        "$"
        );
        if (password.isEmpty()){
            txtPassword.setError("Este campo no puede estar vacio");
            return false;
        }
        else if(!passwordRegex.matcher(password).matches()){
            txtPassword.setError("La contraseña debe incluir un caracter especial,un caracter minuscula , un caracter mayuscula, un numero y poseer minimo 6 caracteres");
            Toast.makeText(this, "La contraseña debe incluir un caracter especial,un caracter minuscula , un caracter mayuscula, un numero y poseer minimo 6 caracteres", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            txtPassword.setError(null);
        }
        return true;
    }
    private boolean verificarUsuario(String nombre){
        if (nombre.isEmpty()){
            txtNombre.setError("Este campo no puede estar vacio");
            return false;
        }
     return true;
    }
    private boolean verificarApellidos(String apellidos){
        if (apellidos.isEmpty()){
            txtApellido.setError("Este campo no puede estar vacio");
            return false;
        }
        return true;
    }
    private void Toastdeerror(String error) {

        switch (error) {


            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(Registro.this, "El formato del token personalizado es incorrecto", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(Registro.this, "El token personalizado no corresponde con el formato", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(Registro.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(Registro.this, "La dirección de correo electrónico está mal escrita", Toast.LENGTH_LONG).show();
                txtCorreo.setError("La dirección de correo electrónico está mal escrita");
                txtCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(Registro.this, "La contraseña no es válida o el usuario no tiene contraseña", Toast.LENGTH_LONG).show();
                txtPassword.setError("la contraseña es incorrecta ");
                txtPassword.requestFocus();
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(Registro.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión previamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(Registro.this,"Esta operación necesita reiniciarse, Inicie sesión nuevamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(Registro.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales, pruebe con otra cuenta", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(Registro.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta", Toast.LENGTH_LONG).show();
                txtCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta");
                txtCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(Registro.this, "Esta credencial ya está asociada con una cuenta de usuario diferente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(Registro.this, "La cuenta de usuario ha sido inhabilitada por el administrador de esta aplicacion", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(Registro.this, "La credencial del usuario ya no es válida, inicie sesión nuevamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(Registro.this, "No hay ningún registro de usuario que corresponda a esta cuenta, Es posible que se haya eliminado esta cuenta", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(Registro.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(Registro.this, "Error, Esta operación no está permitida", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(Registro.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                txtPassword.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                txtPassword.requestFocus();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
             finish();
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setMessage("Esta aplicacion requiere conexion a internet")
                .setCancelable(false)
                .setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Bienvenida.class));
                        finish();
                    }
                });
        builder.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerNetworkCallback(){
        try {

            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){

                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected = false;
                }
            });

        }catch (Exception e){

            isConnected = false;

        }

    }

}