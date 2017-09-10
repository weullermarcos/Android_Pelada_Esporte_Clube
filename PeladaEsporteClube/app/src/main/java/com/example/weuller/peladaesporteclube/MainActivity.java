package com.example.weuller.peladaesporteclube;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION = 0;

    private FirebaseAuth mAuth;

    private Button btnExit, btnMap, btnChat;
    private TextView txtUser;

    private DialogService dialog = new DialogService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);

        btnExit = (Button) findViewById(R.id.main_btnExit);
        btnMap = (Button) findViewById(R.id.main_btnMap);
        btnChat = (Button) findViewById(R.id.main_btnChat);
        txtUser = (TextView) findViewById(R.id.main_txtUser);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //NÀO TEM PERMISSÃO para localização DESABILITA O BOTÃO
            btnMap.setEnabled(false);
        }
        else{

            //TEM PERMISSÃO para localização hABILITA O BOTÃO
            btnMap.setEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser().getDisplayName() != null) {

            String user = mAuth.getCurrentUser().getDisplayName();

            //mAuth.getCurrentUser().getPhotoUrl();

            if(user != null && user.isEmpty())
                user = mAuth.getCurrentUser().getEmail();

            txtUser.setText(user);
        }

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAuth != null)
                    mAuth.signOut();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case REQUEST_FINE_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    btnMap.setEnabled(true);
                }
                else{

                    btnMap.setEnabled(false);
                    dialog.showAlertDialog("Algumas funcionalidades podem não funcionar corretamente, caso a permissão de localização não seja concedida!", "Aviso", MainActivity.this);
                }
                return;
            }
        }
    }


    private void loadPermissions(String perm, int requestCode) {

        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            }
        }
    }
}
