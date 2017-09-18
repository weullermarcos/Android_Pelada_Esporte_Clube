package com.example.weuller.peladaesporteclube;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class ScheduleFootballActivity extends AppCompatActivity {

    private EditText edtDate, edtTime;
    private String selectedDate, selectedTime;
    private Spinner spnType;
    private Button btnFindSuggested;

    ArrayAdapter<String> adpType;

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

        edtDate.setText(getCurrentDate());
        edtTime.setText(getCurrentTime());

        //configurando adapter de Tipos
        adpType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adpType);

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

        adpType.add("Campo");
        adpType.add("Quadra");
        adpType.add("Society");
    }
}
