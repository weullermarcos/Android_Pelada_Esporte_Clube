package com.example.weuller.peladaesporteclube;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Models.FootballField;
import com.example.weuller.peladaesporteclube.Models.FootballMatch;
import com.example.weuller.peladaesporteclube.Services.DialogService;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScheduleFootballActivity extends AppCompatActivity {

    private EditText edtDate, edtTime;
    private String selectedDate, selectedTime;
    private Spinner spnType;
    private LinearLayout lltSuggestedArea;
    private Button btnFindSuggested, btnSchedule, btnCreateAlarm;
    private ListView lstSuggested;
    private ArrayAdapter<String> adpType;
    private ArrayAdapter<FootballField> adpSuggested;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private List<FootballField> footballFields = new ArrayList<>();
    private DialogService dialog = new DialogService();
    private boolean footballFieldUpdated = false;
    private FootballField selectedFootballFieldItem;


    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
        edtDate.setText(selectedDate);
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
        edtTime.setText(selectedTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_football);

        spnType = (Spinner) findViewById(R.id.schedule_football_spnType);
        edtDate = (EditText) findViewById(R.id.schedule_football_edtDate);
        edtTime = (EditText) findViewById(R.id.schedule_football_edtTime);
        btnFindSuggested = (Button) findViewById(R.id.schedule_football_btnFindSuggested);
        btnSchedule = (Button) findViewById(R.id.schedule_football_btnSchedule);
        btnCreateAlarm = (Button) findViewById(R.id.schedule_football_btnCreateAlarm);
        lstSuggested = (ListView) findViewById(R.id.schedule_football_lstSuggested);
        lltSuggestedArea = (LinearLayout) findViewById(R.id.schedule_football_lltSuggestedArea);

        edtDate.setText(getCurrentDate());
        edtTime.setText(getCurrentTime());

        //configurando adapter de Tipos
        adpType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adpType);

        //configurando adapter de quadras sugeridas
        adpSuggested = new ArrayAdapter<FootballField>(this, android.R.layout.simple_list_item_1);
        lstSuggested.setAdapter(adpSuggested);

        populaAdapter();

        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    DatePickerFragment fragment = new DatePickerFragment();
                    fragment.show(getSupportFragmentManager(), "datePicker");
                }
                return true;
            }
        });

        edtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    TimePickerFragment fragment = new TimePickerFragment();
                    fragment.show(getSupportFragmentManager(), "timePicker");
                }
                return true;
            }
        });

        btnFindSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                footballFieldUpdated = false;
                adpSuggested.clear();
                dialog.showProgressDialog("Carregando informações...", "Aguarde", ScheduleFootballActivity.this);
                loadFootballFields();
                lltSuggestedArea.setVisibility(View.VISIBLE);
            }
        });

        btnCreateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = edtDate.getText().toString();
                String time = edtTime.getText().toString();

                String[] dateParts = date.split("/");

                String day = dateParts[0];
                String month = dateParts[1];
                String year = dateParts[2];

                String[] timeParts = time.split(":");

                String hour = timeParts[0];
                String minutes = timeParts[1];


                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.clear();

                //criando alarme para uma hora antes do futebol (ano, mes, dia, hora, minuto)
                cal.set(Integer.parseInt(year),(Integer.parseInt(month) - 1),Integer.parseInt(day),(Integer.parseInt(hour) - 1),Integer.parseInt(minutes));
              //cal.set(2017,8,27,14,8);

                AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(ScheduleFootballActivity.this, AlarmReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleFootballActivity.this, 0, intent, 0);
                // cal.add(Calendar.SECOND, 5);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                footballFieldUpdated = true;

                if(selectedFootballFieldItem == null){

                    Toast.makeText(ScheduleFootballActivity.this, "Favor selecionar uma das quadras sugeridas.", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.showProgressDialog("Marcando Futebol.", "Aguarde", ScheduleFootballActivity.this);

                try {

                    //Atualiza dados da quadra
                    for (FootballField footballField : footballFields) {

                        String userKey = footballField.getKey();


                        DatabaseReference hopperRef = myRef.child(userKey);
                        Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                        hopperUpdates.put("inUse", footballField.getInUse());
                        hopperUpdates.put("isPublic", footballField.getIsPublic());
                        hopperUpdates.put("latitude", footballField.getLatitude());
                        hopperUpdates.put("longitude", footballField.getLongitude());
                        hopperUpdates.put("name", footballField.getName());
                        hopperUpdates.put("suggested", footballField.getSuggested());
                        hopperUpdates.put("type", footballField.getType());
                        hopperUpdates.put("isSelected", "não");

                        if(userKey.equals(selectedFootballFieldItem.getKey()))
                            hopperUpdates.put("isSelected", "sim");

                        hopperRef.updateChildren(hopperUpdates);
                    }

                    //atualiza dados da partida marcada
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("footballMatch");
                    DatabaseReference hopperRef = myRef.child("00FOOTBALLMATCH0001");
                    Map<String, Object> hopperUpdates = new HashMap<String, Object>();

                    hopperUpdates.put("date", edtDate.getText().toString());
                    hopperUpdates.put("footballFieldId", selectedFootballFieldItem.getKey());
                    hopperUpdates.put("footballFieldLatitude", selectedFootballFieldItem.getLatitude());
                    hopperUpdates.put("footballFieldLongitude", selectedFootballFieldItem.getLongitude());
                    hopperUpdates.put("footballFieldName", selectedFootballFieldItem.getName());
                    hopperUpdates.put("hour", edtTime.getText().toString());
                    hopperRef.updateChildren(hopperUpdates);

                    LinearLayout.LayoutParams param;

                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );

                    btnSchedule.setLayoutParams(param);

                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.0f
                    );

                    btnCreateAlarm.setLayoutParams(param);

                    Toast.makeText(ScheduleFootballActivity.this, "Futebol marcado.", Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(ScheduleFootballActivity.this, "Erro ao marcar futebol", Toast.LENGTH_SHORT).show();
                }
                finally {
                    dialog.hideProgressDialog();
                }
            }
        });

        lstSuggested.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectedFootballFieldItem = (FootballField) lstSuggested.getItemAtPosition(position);

                //muda cor de fundo da linha selecionada
                for (int i = 0; i < lstSuggested.getChildCount(); i++) {
                    if(position == i ){
                        lstSuggested.getChildAt(i).setBackgroundColor(Color.parseColor("#689F38"));
                    }else{
                        lstSuggested.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }


            }
        });

    }

    private String getCurrentDate(){

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }

    private String getCurrentTime(){

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return Integer.toString(hour) + ":" + Integer.toString(minute);
    }

    private void populaAdapter() {

        adpType.add("Quadra");
        adpType.add("Society");
        adpType.add("Campo");
    }

    public void loadFootballFields() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("footballField");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    if(footballFieldUpdated)
                        return;

                    footballFields.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        FootballField footballField = postSnapshot.getValue(FootballField.class);
                        footballField.setKey(postSnapshot.getKey());
                        footballFields.add(footballField);
                    }

                    //TODO: (futuramente) requisitar do servidor as quadras sugeridas baseadas em um algoritmo, atualmente está sendo feito de forma aleatoria

                    Random gerador = new Random();
                    int num1 = gerador.nextInt(footballFields.size() - 1);
                    int num2 = gerador.nextInt(footballFields.size() - 1);
                    int count = 0;

                    for (FootballField footballField : footballFields) {

                        if((count == num1 || count ==num2) &&
                                footballField.getType().toLowerCase().equals(spnType.getSelectedItem().toString().toLowerCase())){

                            //adiciona quadra sugerida
                            adpSuggested.add(footballField);
                        }

                        count ++;
                    }

                } catch (Exception e) {
                    Toast.makeText(ScheduleFootballActivity.this, "Erro ao carregar informações de quadras.", Toast.LENGTH_SHORT).show();
                } finally {
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(ScheduleFootballActivity.this, "Erro ao carregar informações de quadras. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
