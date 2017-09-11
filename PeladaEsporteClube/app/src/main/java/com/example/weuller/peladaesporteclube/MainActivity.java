package com.example.weuller.peladaesporteclube;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Models.User;
import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Location currentLocation;

    private Button btnExit, btnMap, btnChat;
    private TextView txtUser;

    private DialogService dialog = new DialogService();
    private List<User> userList = new ArrayList<>();

    private boolean userExists = false;
    private boolean userCreatedOrUpdated = false;
    private String userKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = (Button) findViewById(R.id.main_btnExit);
        btnMap = (Button) findViewById(R.id.main_btnMap);
        btnChat = (Button) findViewById(R.id.main_btnChat);
        txtUser = (TextView) findViewById(R.id.main_txtUser);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        dialog.showProgressDialog("Carregando suas informações...", "Aguarde", MainActivity.this);

        if(mAuth.getCurrentUser().getDisplayName() != null) {

            String user = mAuth.getCurrentUser().getDisplayName();
            //mAuth.getCurrentUser().getPhotoUrl();

            if(user != null && user.isEmpty())
                user = mAuth.getCurrentUser().getEmail();

            txtUser.setText(user);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //NÀO TEM PERMISSÃO para localização DESABILITA O BOTÃO
            btnMap.setEnabled(false);

            //como não foi habilitada a permissão para uso de localização não será atualizada as informações de localização
            userCreatedOrUpdated = true;
        }
        else{

            //TEM PERMISSÃO para localização hABILITA O BOTÃO
            btnMap.setEnabled(true);
            userCreatedOrUpdated = false;
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(userCreatedOrUpdated) {
                    dialog.hideProgressDialog();
                    return;
                }
                    try {

                    userList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        userKey = postSnapshot.getKey();
                        User user = postSnapshot.getValue(User.class);

                        userList.add(user);

                        if(user.getUid().equals(mAuth.getCurrentUser().getUid())){
                            userExists = true;
                            break;
                        }
                    }

                    getCurrentLocation();
                }

                catch (Exception e){
                    dialog.hideProgressDialog();
                }
                finally {
                        dialog.hideProgressDialog();
                    }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(MainActivity.this, "Erro ao carregar informações do usuário. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
            }
        });


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




    private void updateOrPushUserData(){

        //atualiza latitude e longitude do usuário
        if(userExists){

            //TODO: testar melhor atualizaçao de localizacao
            DatabaseReference hopperRef = myRef.child(userKey);
            Map<String, Object> hopperUpdates = new HashMap<String, Object>();
            hopperUpdates.put("latitude", currentLocation.getLatitude());
            hopperUpdates.put("longitude", currentLocation.getLongitude());

            hopperRef.updateChildren(hopperUpdates);

        }
        else{

            //registra dados do usuário
            User user = new User();
            user.setName(mAuth.getCurrentUser().getDisplayName());
            user.setEmail(mAuth.getCurrentUser().getEmail());
            user.setUid(mAuth.getCurrentUser().getUid());
            user.setLatitude(currentLocation.getLatitude());
            user.setLongitude(currentLocation.getLongitude());

            DatabaseReference newPostRef = myRef.push();
            newPostRef.setValue(user);

        }

        userCreatedOrUpdated = true;
        dialog.hideProgressDialog();
    }

    private void getCurrentLocation() {

        try {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {

                        Log.d("LATITUDE", String.valueOf(location.getLatitude()));
                        Log.d("LONGITUDE", String.valueOf(location.getLongitude()));

                        currentLocation = location;

                        updateOrPushUserData();
                    }
                }
            });

        } catch (SecurityException e) {

            dialog.showAlertDialog("Verifique se o seu GPS está ligado ou se você tem as permissões necessárias para usar esse recurso.", "Erro", MainActivity.this);
        }
    }
}
