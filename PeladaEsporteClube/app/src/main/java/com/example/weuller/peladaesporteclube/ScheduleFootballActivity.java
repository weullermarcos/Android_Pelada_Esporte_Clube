package com.example.weuller.peladaesporteclube;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ScheduleFootballActivity extends AppCompatActivity {

    private Button btnTimePicker, btnDatePicker;
    private String selectedDate, selectedTime;

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_football);

        btnTimePicker = (Button) findViewById(R.id.btnTimePicker);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);


        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerFragment fragment = new TimePickerFragment();
                fragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "datePicker");

            }
        });
    }
}
