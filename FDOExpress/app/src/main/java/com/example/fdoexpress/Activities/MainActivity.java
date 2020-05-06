package com.example.fdoexpress.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.LoginRegisterAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.BufferedReader;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    String recibido = "";
    int codigoUsuario = 1;
    Socket socket;
    static EditText editText;
    static EditText editPass;
    Button blogin;
    Button bregister;
    private BufferedReader in;
    private SharedPreferences preferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);

        if(getLoginState()){
            String enviar = 0 +"&"+preferences.getString(Constantes.USER_NAME,"")+"&"+preferences.getString(Constantes.USER_PASSWORD,"");
            LoginRegisterAsyncTask log = new LoginRegisterAsyncTask(new PeticionListener() {
                @Override
                public void callback(String accion) {
                    tratarMensaje(accion);
                }
            },enviar);
            log.execute();
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
                        tratarMensaje(accion);
                    }
                },enviar);
                log.execute();
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
                //Si el login es correcto se mostraría al cliente el activity menu
                saveLoginState(argumentos[4],argumentos[5]);
                Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
                Intent intent   = new Intent(MainActivity.this, MenuSlideActivity.class);

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(intent,activityOptions.toBundle());

                finish();
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
                preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,false).apply();
                preferences.edit().commit();
                Toast.makeText(this, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean getLoginState(){
        SharedPreferences preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        return preferences.getBoolean(Constantes.PREFERENCE_LOGIN_STATE,false);
    }

    public void saveLoginState(String name, String password){
        preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,true).apply();
        preferences.edit().putString(Constantes.USER_NAME,name).apply();
        preferences.edit().putString(Constantes.USER_PASSWORD,password).apply();
        preferences.edit().commit();
    }
}
