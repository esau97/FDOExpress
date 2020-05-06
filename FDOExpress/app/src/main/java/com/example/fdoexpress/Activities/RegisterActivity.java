package com.example.fdoexpress.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.LoginRegisterAsyncTask;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class RegisterActivity extends AppCompatActivity {

    static Button bregister;
    static EditText editName;
    static EditText editPass;
    static EditText editAddress;
    static EditText editTfn;
    static EditText editUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo);
        setContentView(R.layout.activity_register);
        bregister = this.findViewById(R.id.bregister);
        editName = this.findViewById(R.id.edit_name);
        editPass = this.findViewById(R.id.edit_pass);
        editUser = this.findViewById(R.id.edit_user);
        editAddress = this.findViewById(R.id.edit_address);
        editTfn = this.findViewById(R.id.edit_tfno);
        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String password = editPass.getText().toString().trim();
                String username = editUser.getText().toString().trim();
                String address = editAddress.getText().toString().trim();
                String tfn = editTfn.getText().toString().trim();
                String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                String enviar = 1 +"&"+ name +"&"+ username + "&"+passwordCodif+"&"+address+"&"+tfn+"&3";

                LoginRegisterAsyncTask log = new LoginRegisterAsyncTask(new PeticionListener() {
                    @Override
                    public void callback(String accion) {

                    }
                },enviar);
                log.execute();
            }
        });
    }
}
