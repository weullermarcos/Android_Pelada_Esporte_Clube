package com.example.weuller.peladaesporteclube;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = (EditText) findViewById(R.id.register_edtName);
        edtEmail = (EditText) findViewById(R.id.register_edtEmail);
        edtPassword = (EditText) findViewById(R.id.register_edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.register_edtConfirmPassword);
        btnSend = (Button) findViewById(R.id.register_btnSend);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: fazer as validações e enviar ao firebase

                if(edtEmail.getText().toString().trim().isEmpty() ||
                        edtName.getText().toString().trim().isEmpty() ||
                        edtPassword.getText().toString().trim().isEmpty() ||
                        edtConfirmPassword.getText().toString().trim().isEmpty()) {

                    Toast.makeText(RegisterActivity.this, "Favor preencher todos os campos..", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


    }
}
