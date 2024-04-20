package com.lemtproyecto.casadomotica;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;


public class Login extends Activity {
    TextView txtCrear,txtRecuperar;
    TextInputLayout txtCorreo, txtPassword;
    Button btnLogin;

    public static String EMAIL = "email";
    public static String PREFERENCIAS = "preferencias";
    FirebaseAuth firebaseAuth;
    ConnectivityManager connectivityManager;
    boolean isConnected=false;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.day));
        }
        registerNetworkCallback();
        txtCrear = findViewById(R.id.txtCC);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        txtRecuperar = findViewById(R.id.txtRecuperar);
        pd = new ProgressDialog(Login.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        pd.setTitle("En Proceso");
        pd.setMessage("Esto tomara unos instantes");
        pd.setCancelable(false);

        btnLogin = findViewById(R.id.btnIngresar);
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                registerNetworkCallback();
                if (!isConnected) {
                    showDialog();
                } else {

                    String correo = txtCorreo.getEditText().getText().toString();
                    String password = txtPassword.getEditText().getText().toString();
                    boolean a = esCorreoValido(correo);
                    boolean b = esPaswordValido(password);

                    if (a && b) {
                        firebaseAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                        saveData();
                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else {
                                        Toast.makeText(Login.this, "Revise su bandeja de correo para realizar la validacion de la cuenta", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    Toastdeerror(errorCode);
                                }
                            }
                        });
                    }
                }
            }
        });
        txtCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Registro.class);
                startActivity(i);

            }
        });
        txtRecuperar.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
         public void onClick(View v) {

             registerNetworkCallback();
                if (!isConnected) {
                    showDialog();
                } else {
                    recuperarContraseña();
                }
            }


        });
    }

    public void recuperarContraseña() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.cuadro_recuperar_passsword, null);
        builder.setView(dialogview);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextInputLayout edtCorreoCambiar = dialogview.findViewById(R.id.txtEmailRecup);
        Button btnCerrar = dialogview.findViewById(R.id.btnCerrarD);
        Button btnSend = dialogview.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correRecuperar=edtCorreoCambiar.getEditText().getText().toString();
                boolean z;
                if (correRecuperar.isEmpty()) {
                    edtCorreoCambiar.setError("Este campo no puede estar vacio");
                    Toast.makeText(Login.this, "vao", Toast.LENGTH_SHORT).show();
                    z=false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(correRecuperar).matches()) {
                    edtCorreoCambiar.setError("Correo electrónico inválido");
                    z=false;
                } else {
                    edtCorreoCambiar.setError(null);
                    z=true;
                }
                if (z){
                    pd.show();
                    firebaseAuth.setLanguageCode("es");
                    firebaseAuth.sendPasswordResetEmail(correRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Login.this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(Login.this, "No se pudo encontrar la direccion especificada, ingresela nuevamente en el campo de correo", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                        }
                    });
                }
            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private boolean esCorreoValido(String correo) {

        if (correo.isEmpty()) {
            txtCorreo.setError("Este campo no puede estar vacio");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Correo electrónico inválido");
            return false;
        } else {
            txtCorreo.setError(null);
        }
        return true;
    }

    private void Toastdeerror(String error) {

        switch (error) {


            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(Login.this, "El formato del token personalizado es incorrecto", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(Login.this, "El token personalizado no corresponde con el formato", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(Login.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(Login.this, "La dirección de correo electrónico está mal escrita", Toast.LENGTH_LONG).show();
                txtCorreo.setError("La dirección de correo electrónico está mal escrita");
                txtCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(Login.this, "La contraseña no es válida o el usuario no tiene contraseña", Toast.LENGTH_LONG).show();
                txtPassword.setError("la contraseña es incorrecta ");
                txtPassword.requestFocus();
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(Login.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión previamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(Login.this, "Esta operación necesita reiniciarse, Inicie sesión nuevamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(Login.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales, pruebe con otra cuenta", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(Login.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta", Toast.LENGTH_LONG).show();
                txtCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta");
                txtCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(Login.this, "Esta credencial ya está asociada con una cuenta de usuario diferente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(Login.this, "La cuenta de usuario ha sido inhabilitada por el administrador de esta aplicacion", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(Login.this, "La credencial del usuario ya no es válida, inicie sesión nuevamente", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(Login.this, "No hay ningún registro de usuario que corresponda a esta cuenta, Es posible que se haya eliminado esta cuenta", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(Login.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(Login.this, "Error, Esta operación no está permitida", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(Login.this, "La contraseña proporcionada no es válida", Toast.LENGTH_LONG).show();
                txtPassword.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                txtPassword.requestFocus();
                break;
            case "INTERNAL_ERROR":
                Toast.makeText(Login.this, "Error al procesar la solicitud", Toast.LENGTH_LONG).show();
                break;

        }

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, txtCorreo.getEditText().getText().toString());
        editor.commit();
    }

    private boolean esPaswordValido(String password) {

        if (password.isEmpty()) {
            txtPassword.setError("Este campo no puede estar vacio");
            return false;
        } else {
            txtPassword.setError(null);
        }
        return true;
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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





