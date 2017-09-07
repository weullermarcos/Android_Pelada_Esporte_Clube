package com.example.weuller.peladaesporteclube;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button btnExit;
    private TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = (Button) findViewById(R.id.main_btnExit);
        txtUser = (TextView) findViewById(R.id.main_txtUser);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser().getDisplayName() != null) {

            String user = mAuth.getCurrentUser().getDisplayName();

            if(user != null && user.isEmpty())
                user = mAuth.getCurrentUser().getEmail();

            txtUser.setText(user);
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAuth != null)
                    mAuth.signOut();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }
}