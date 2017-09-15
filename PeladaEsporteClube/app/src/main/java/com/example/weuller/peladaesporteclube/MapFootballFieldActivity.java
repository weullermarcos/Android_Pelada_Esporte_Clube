package com.example.weuller.peladaesporteclube;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Models.FootballField;
import com.example.weuller.peladaesporteclube.Models.User;
import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapFootballFieldActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<User> userList = new ArrayList<>();
    private List<FootballField> footballFields = new ArrayList<>();
    private DialogService dialog = new DialogService();
    private LatLng myLocation = new LatLng(0.00, 0.00);

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private boolean iscCentered = false;

    private LinearLayout lltMap, lltBottomScreen;

    Fragment fgmtBottomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_football_field);

        lltMap = (LinearLayout) findViewById(R.id.map_football_field_lltMap);
        lltBottomScreen = (LinearLayout) findViewById(R.id.map_football_field_lltBottomScreen);
//        fgmtBottomMap = (Fragment) findViewById(R.id.map_football_field_fgmtBottomMap);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LinearLayout.LayoutParams param;
                FootballField footballField = (FootballField) marker.getTag();

                if(footballField != null)
                {
                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            30.0f
                    );
                }
                else{

                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0.0f
                    );

                }

                lltMap.setLayoutParams(param);

                return false;
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        dialog.showProgressDialog("Carregando informações...", "Aguarde", MapFootballFieldActivity.this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    userList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        User user = postSnapshot.getValue(User.class);
                        userList.add(user);
                    }

                    mMap.clear();

                    for (User user: userList) {

                        LatLng location = new LatLng(user.getLatitude(), user.getLongitude());

                        if(user.getName().equals(mAuth.getCurrentUser().getDisplayName())) {
                            mMap.addMarker(new MarkerOptions().position(location).title("EU"));
                            myLocation = location;
                        }
                        else {

                            mMap.addMarker(new MarkerOptions().position(location).title(user.getName()));
                        }
                    }


                    if(!iscCentered) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f));
                        iscCentered = true;
                    }

                    loadFootballFields();
                }

                catch (Exception e){
                    Toast.makeText(MapFootballFieldActivity.this, "Erro ao carregar informações do usuário.", Toast.LENGTH_SHORT).show();
                }
                finally {
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(MapFootballFieldActivity.this, "Erro ao carregar informações do usuário. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadFootballFields(){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("footballField");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    footballFields.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        FootballField footballField = postSnapshot.getValue(FootballField.class);
                        footballFields.add(footballField);
                    }

                    //TODO: criar algoritmo para determinar quadras recomendadas, atualmente está sendo aleatório
                    //gera um número aleatório
                    Random gerador = new Random();
                    int num1 = gerador.nextInt(footballFields.size() - 1);
                    int num2 = gerador.nextInt(footballFields.size() - 1);
                    int count = 0;

                    for (FootballField footballField : footballFields) {

                        LatLng location = new LatLng(footballField.getLatitude(), footballField.getLongitude());

                        Marker myMarker;

                        //se for uma quadra seleciona pinta de azul
                        if(count == num1 || count ==num2){

                            myMarker = mMap.addMarker(
                                       new MarkerOptions()
                                               .position(location)
                                               .title(footballField.getName())
                                               .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }
                        else {

                            myMarker = mMap.addMarker(
                                       new MarkerOptions()
                                               .position(location)
                                               .title(footballField.getName())
                                               .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        }

                        myMarker.setTag(footballField);

                        count ++;
                    }
                }

                catch (Exception e){
                    Toast.makeText(MapFootballFieldActivity.this, "Erro ao carregar informações de quadras.", Toast.LENGTH_SHORT).show();
                }
                finally {
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(MapFootballFieldActivity.this, "Erro ao carregar informações de quadras. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

