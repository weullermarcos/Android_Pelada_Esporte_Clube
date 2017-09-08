package com.example.weuller.peladaesporteclube;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSend;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = (EditText) findViewById(R.id.register_edtName);
        edtEmail = (EditText) findViewById(R.id.register_edtEmail);
        edtPassword = (EditText) findViewById(R.id.register_edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.register_edtConfirmPassword);
        btnSend = (Button) findViewById(R.id.register_btnSend);

        mAuth = FirebaseAuth.getInstance();


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

                if(!edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {

                    Toast.makeText(RegisterActivity.this, "As senhas digitadas não conferem", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("LOG", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    Toast.makeText(RegisterActivity.this, "Usuário criado com sucesso.",
                                            Toast.LENGTH_SHORT).show();

                                } else {

                                    // If sign in fails, display a message to the user.
                                    Log.w("LOG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Erro ao criar usuário.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // [START_EXCLUDE]
                                //hideProgressDialog();
                                // [END_EXCLUDE]
                            }
                        });
            }
        });


    }
}
