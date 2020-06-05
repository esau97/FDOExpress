package com.example.fdoexpress.Activities;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.LoginRegisterAsyncTask;

import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Tengo que crear un activity al que le paso la direccion IP y almacenarla
 * en el SharedPreferences para poder ser usada posteriormente.
 */

public class MainActivity extends AppCompatActivity {

    static EditText editText;
    static EditText editPass;
    private Button blogin;
    private Button bregister;
    private SharedPreferences preferences ;
    private LottieAnimationView lottieAnimationView;
    private Toolbar toolbar;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        lottieAnimationView = findViewById(R.id.loadingAnimation);
        toolbar = findViewById(R.id.toolBar);
        scrollView = findViewById(R.id.scroll);

        if(getLoginState()){
            String enviar = 0 +"&"+preferences.getString(Constantes.USER_NAME,"")+"&"+preferences.getString(Constantes.USER_PASSWORD,"");
            LoginRegisterAsyncTask log = new LoginRegisterAsyncTask(new PeticionListener() {
                @Override
                public void callback(String accion) {
                    tratarMensaje(accion);
                }
            },enviar);
            log.execute();
        }else{
            toolbar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
        }

        blogin = this.findViewById(R.id.blogin);
        bregister = this.findViewById(R.id.bregister);
        editText= this.findViewById(R.id.edit_usuario);
        editPass = this.findViewById(R.id.edit_pass);
        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editText.getText().toString().trim();
                String password = editPass.getText().toString().trim();
                final String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                String enviar = 0 +"&"+ nombre +"&"+ passwordCodif;
                Log.i("Contraseña",passwordCodif);

                LoginRegisterAsyncTask log = new LoginRegisterAsyncTask(new PeticionListener() {
                    @Override
                    public void callback(String accion) {
                        tratarMensaje(accion+"&"+passwordCodif);
                    }
                },enviar);
                log.execute();
                toolbar.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
            }
        });
        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent   = new Intent(MainActivity.this,RegisterActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(intent,activityOptions.toBundle());
            }
        });
    }

    public void tratarMensaje(String codigo) {
        String respuesta = "";
        String argumentos[] = codigo.split("&");
        System.out.println("Recibido"+codigo);
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case LOGIN_CORRECTO:
                lottieAnimationView.setVisibility(View.GONE);
                System.out.println("Acabado de recibir"+codigo);
                //Si el login es correcto se mostraría al cliente el activity menu
                // Además el servidor me devuelve una lista con todos los pedidos
                // por lo que debo cargar los datos en el recyclerView
                if(argumentos[1].equals("2")){
                    saveLoginState(argumentos[1],argumentos[4],argumentos[7],argumentos[8]);
                    Toast.makeText(this, "Login Correcto Receptor", Toast.LENGTH_SHORT).show();
                    Intent intent   = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("JSON",argumentos[6]);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(intent,activityOptions.toBundle());

                    finish();
                }else if(argumentos[1].equals("3")){
                    System.out.println("RECIBIDO"+codigo);
                    saveLoginState(argumentos[3],argumentos[4],argumentos[6],argumentos[7]);
                    Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
                    Intent intent  = new Intent(MainActivity.this, MenuTrabajadorActivity.class);
                    intent.putExtra("JSON",argumentos[5]);
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(intent,activityOptions.toBundle());

                    finish();
                }

                break;
            case REGISTRADO:
                Log.i("Registro","Registrado con éxito");
                break;
            case ERROR:
                mostrarError(Integer.parseInt(argumentos[1]));
                break;
        }
    }

    public void mostrarError(int codigoError){
        switch (codigoError) {
            case 1:
                toolbar.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
                preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,false).apply();
                preferences.edit().commit();
                Toast.makeText(this, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Conexión con servidor no disponible.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean getLoginState(){
        SharedPreferences preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        return preferences.getBoolean(Constantes.PREFERENCE_LOGIN_STATE,false);
    }

    public void saveLoginState(String codigoUsuario,String tfno,String name, String password){
        preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,true).apply();
        preferences.edit().putString(Constantes.USER_NAME,name).apply();
        preferences.edit().putString(Constantes.USER_PASSWORD,password).apply();
        preferences.edit().putString(Constantes.USER_PHONE,tfno).apply();
        preferences.edit().putString(Constantes.USER_CODE,codigoUsuario).apply();
        //preferences.edit().putString(Constantes.USER_TYPE,tipo);
        preferences.edit().commit();
    }
}
