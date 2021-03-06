package com.example.weuller.peladaesporteclube.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Fragments.BottomMapFragment;
import com.example.weuller.peladaesporteclube.Models.FootballField;
import com.example.weuller.peladaesporteclube.Models.FootballMatch;
import com.example.weuller.peladaesporteclube.Models.User;
import com.example.weuller.peladaesporteclube.R;
import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class MapFootballFieldActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<User> userList = new ArrayList<>();
    private List<FootballField> footballFields = new ArrayList<>();
    private List<FootballMatch> footballMaths = new ArrayList<>();
    private DialogService dialog = new DialogService();
    private LatLng myLocation = new LatLng(0.00, 0.00);

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private boolean iscCentered = false;

    private LinearLayout lltMap, lltBottomScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_football_field);

        lltMap = (LinearLayout) findViewById(R.id.map_football_field_lltMap);
        lltBottomScreen = (LinearLayout) findViewById(R.id.map_football_field_lltBottomScreen);

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
                String date = "00/00/0000";
                String hour = "00:00";

                if(footballMaths.size() > 0){
                    FootballMatch match = footballMaths.get(0);

                    if(match != null){

                        date = match.getDate();
                        hour = match.getHour();
                    }
                }


                if(footballField != null)
                {
                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            30.0f
                    );

                    BottomMapFragment bottomMapFragment = (BottomMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_football_field_fgmtBottomMap);
                    bottomMapFragment.changeParams(footballField, date, hour);

                    if(footballField.getIsSelected().toLowerCase().equals("sim")){
                        bottomMapFragment.setLltTimeVisible();
                    }
                    else {
                        bottomMapFragment.setLltTimeInvisible();
                    }

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                LinearLayout.LayoutParams param;
                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.0f
                );

                lltMap.setLayoutParams(param);
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

                        String userName = "Usuário";

                        if(!user.getName().isEmpty()){
                            userName = user.getName();
                        }

                        LatLng location = new LatLng(user.getLatitude(), user.getLongitude());

                        if(userName.equals(mAuth.getCurrentUser().getDisplayName())) {
                            mMap.addMarker(new MarkerOptions().position(location).title("EU"));
                            myLocation = location;
                        }
                        else {

                            mMap.addMarker(new MarkerOptions().position(location).title(userName));
                        }
                    }


                    if(!iscCentered) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f));
                        iscCentered = true;
                    }

                    loadFootballFields();
                    loadFootballMath();
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

                        LatLng location = new LatLng(footballField.getLatitude(), footballField.getLongitude());
                        Marker myMarker;


                        //se for a quadra que marcou o futebol marca de azul
                        if(footballField.getIsSelected().toLowerCase().equals("sim")){

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

    public void loadFootballMath() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("footballMatch");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    footballMaths.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        FootballMatch match = postSnapshot.getValue(FootballMatch.class);
                        footballMaths.add(match);
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