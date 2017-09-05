package com.example.weuller.peladaesporteclube;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSigin, btnRegister;
    SignInButton btnGoogle;

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.login_edtEmail);
        edtPassword = (EditText) findViewById(R.id.login_edtPassword);
        btnSigin = (Button) findViewById(R.id.login_btnSigin);
        btnRegister = (Button) findViewById(R.id.login_btnRegister);
        btnGoogle = (SignInButton) findViewById(R.id.login_btnGoogle);


        mAuth = FirebaseAuth.getInstance();

        btnSigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty()) {

                    Toast.makeText(LoginActivity.this, "Favor preencher todos os campos..", Toast.LENGTH_SHORT).show();
                    return;
                }

                //FUNCIONANDO
                mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(! task.isSuccessful()){

                            Toast.makeText(LoginActivity.this, "Usuário ou senha inválida", Toast.LENGTH_SHORT).show();
                        }else{

                            user = task.getResult().getUser();

                            if(user != null){
                                goToMain();
                            }
                        }
                    }
                });

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Log.d("LOG", "onAuthStateChanged:signed_in:" + user.getUid());
                        } else {
                            // User is signed out
                            Log.d("LOG", "onAuthStateChanged:signed_out");
                        }
                    }
                };

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }

    private void goToMain(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
