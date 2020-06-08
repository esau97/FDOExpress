package com.example.fdoexpress.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MainAsyncTask;

import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    static Button bregister;
    static EditText editName;
    static EditText editPass;
    static EditText editAddress;
    static EditText editTfn;
    static EditText editUser;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        setContentView(R.layout.activity_register);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        bregister = this.findViewById(R.id.bregister);
        editName = this.findViewById(R.id.edit_name);
        editPass = this.findViewById(R.id.edit_pass);
        editUser = this.findViewById(R.id.edit_user);
        editAddress = this.findViewById(R.id.edit_address);
        editTfn = this.findViewById(R.id.edit_tfno);
        editUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!formatoCorrectoCorreo(editUser.getText().toString())){
                    editUser.setError("Compruebe el formato del correo.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String password = editPass.getText().toString().trim();
                String username = editUser.getText().toString().trim();
                String address = editAddress.getText().toString().trim();
                String tfn = editTfn.getText().toString().trim();
                final String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                String enviar = 19 +"&"+ name +"&"+ username + "&"+passwordCodif+"&"+address+"&"+tfn+"&3";
                if(!formatoCorrectoCorreo(username)){
                    Toast.makeText(RegisterActivity.this, "Compruebe el formato del correo", Toast.LENGTH_SHORT).show();
                }else if(name.equals("") || password.equals("") || username.equals("") || address.equals("")
                        || tfn.equals("")){
                    Toast.makeText(RegisterActivity.this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                }else{
                    MainAsyncTask log = new MainAsyncTask(new PeticionListener() {
                        @Override
                        public void callback(String accion) {
                            tratarMensaje(accion+"&"+passwordCodif);
                        }
                    },enviar);
                    log.execute();
                }
            }
        });
    }

    public void tratarMensaje(String codigo){
        String argumentos[] = codigo.split("&");
        System.out.println("Recibido"+codigo);
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case REGISTRADO:
                Toast.makeText(this, "Registrado", Toast.LENGTH_SHORT).show();
                // Intent inicio sesion
                saveLoginState(argumentos[5],argumentos[4],argumentos[2],argumentos[6]);
                Toast.makeText(this, "Login Correcto Receptor", Toast.LENGTH_SHORT).show();
                Intent intent   = new Intent(RegisterActivity.this, MenuActivity.class);
                intent.putExtra("JSON","{[]}");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this);
                startActivity(intent,activityOptions.toBundle());
                finish();
                break;
            case ERROR:
                mostrarError(Integer.parseInt(argumentos[1]));
                break;
        }
    }

    public void mostrarError(int codigoError){
        switch (codigoError) {
            case 2:
                Toast.makeText(this, "El usuario ya ha sido registrado. Escoja otro nombre", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "Conexión con servidor no disponible.", Toast.LENGTH_SHORT).show();
                break;
        }
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

    public boolean formatoCorrectoCorreo(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
