package com.example.weuller.peladaesporteclube;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Models.FootballField;
import com.example.weuller.peladaesporteclube.Models.User;
import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<User> userList = new ArrayList<>();
    private List<FootballField> footballFields = new ArrayList<>();
    private DialogService dialog = new DialogService();
    private LatLng myLocation = new LatLng(0.00, 0.00);

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private boolean iscCentered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        dialog.showProgressDialog("Carregando informações...", "Aguarde", MapsActivity.this);

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
                        iscCentered = true;
                    }

                    loadFootballFields();
                }

                catch (Exception e){
                    Toast.makeText(MapsActivity.this, "Erro ao carregar informações do usuário.", Toast.LENGTH_SHORT).show();
                }
                finally {
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(MapsActivity.this, "Erro ao carregar informações do usuário. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
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

                        mMap.addMarker(
                                    new MarkerOptions()
                                            .position(location)
                                            .title(footballField.getName())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            );
                    }
                }

                catch (Exception e){
                    Toast.makeText(MapsActivity.this, "Erro ao carregar informações de quadras.", Toast.LENGTH_SHORT).show();
                }
                finally {
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(MapsActivity.this, "Erro ao carregar informações de quadras. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
