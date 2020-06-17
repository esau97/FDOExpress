package com.example.fdoexpress.uiProveedor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import com.example.fdoexpress.Activities.MainActivity;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;

public class ProveedorActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private Button button,btnDesconect;
    private EditText nombre,direccion,telefono,localidad;
    private SharedPreferences preferences;
    private View root;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinnerEstado);
        String lista [] = {"1.- Recogida", "2.- Devolución"};
        ArrayAdapter<String> valoresSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lista);
        valoresSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(valoresSpinner);
        spinner.setSelection(0);
        toolbar.setTitle("Datos pedidos");
        setSupportActionBar(toolbar);
        scrollView = findViewById(R.id.scroll);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        button = findViewById(R.id.benviar);
        nombre = findViewById(R.id.edit_name);
        localidad = findViewById(R.id.edit_address);
        telefono = findViewById(R.id.edit_tfno);
        direccion = findViewById(R.id.edit_fullAddress);
        scrollView.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spinner.getSelectedItem().toString();
                String estado;
                if(!nombre.getText().toString().equals("")&& !localidad.getText().toString().equals("") && !direccion.getText().toString().equals("") && !telefono.getText().toString().equals("")){
                    if(text.substring(0,1).equals("2")){
                        estado="7";
                    }else{
                        estado="1";
                    }
                    String enviar = "8&"+ nombre.getText().toString()+"&"+localidad.getText().toString()+","+direccion.getText().toString()+"&"+telefono.getText().toString()+"&"+preferences.getString(Constantes.USER_NAME,"")+"&"+estado;
                    MainAsyncTask mainAsyncTask = new MainAsyncTask(new PeticionListener() {
                        @Override
                        public void callback(String accion) {
                            tratarMensaje(accion);
                        }
                    },enviar);
                    mainAsyncTask.execute();
                }else{
                    Toast.makeText(ProveedorActivity.this, "Debes rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_proveedor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                SharedPreferences preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
                preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,false).apply();
                Intent intent   = new Intent(ProveedorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
