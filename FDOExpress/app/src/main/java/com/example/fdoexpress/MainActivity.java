package com.example.fdoexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fdoexpress.Tasks.LoginRegisterAsyncTask;
import com.example.fdoexpress.Utils.Codigos;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        setContentView(R.layout.activity_main);
        blogin = this.findViewById(R.id.blogin);
        bregister = this.findViewById(R.id.bregister);
        editText= this.findViewById(R.id.edit_usuario);
        editPass = this.findViewById(R.id.edit_pass);
        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editText.getText().toString().trim();
                String password = editPass.getText().toString().trim();
                String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
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
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case LOGIN_CORRECTO:
                //Si el login es correcto se mostraría al cliente el activity menu
                Log.i("Login","Login correcto");
                Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
