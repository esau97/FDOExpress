package com.example.fdoexpress.uiProveedor;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;
import com.google.android.material.snackbar.Snackbar;

public class ProveedorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrollView scrollView;
    private Button button;
    private EditText nombre,direccion,telefono;
    private SharedPreferences preferences;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        toolbar = findViewById(R.id.toolBar);
        scrollView = findViewById(R.id.scroll);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        button = findViewById(R.id.benviar);
        nombre = findViewById(R.id.edit_name);
        direccion = findViewById(R.id.edit_address);
        telefono = findViewById(R.id.edit_tfno);
        toolbar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enviar = "8&"+ nombre.getText().toString()+"&"+direccion.getText().toString()+"&"+telefono.getText().toString()+"&"+preferences.getString(Constantes.USER_NAME,"");
                MainAsyncTask mainAsyncTask = new MainAsyncTask(new PeticionListener() {
                    @Override
                    public void callback(String accion) {
                        tratarMensaje(accion);
                    }
                },enviar);
                mainAsyncTask.execute();
            }
        });
    }

    public void tratarMensaje(String codigo){

        String argumentos[] = codigo.split("&");
        System.out.println("Recibido"+codigo);
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case NUEVO_PEDIDO:
                Toast.makeText(this,"Datos enviados correctamente.", Toast.LENGTH_SHORT).show();
                nombre.setText("");
                direccion.setText("");
                telefono.setText("");
                break;
            case ERROR:
                mostrarError(Integer.parseInt(argumentos[1]));
                break;
        }
    }

    public void mostrarError(int codigoError){
        switch (codigoError) {
            case 1:
                Toast.makeText(this, "No se ha podido dar de alta el pedido", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
