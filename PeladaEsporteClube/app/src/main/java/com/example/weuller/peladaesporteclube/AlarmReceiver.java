package com.example.weuller.peladaesporteclube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by weuller on 27/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Disparou o Alarme", Toast.LENGTH_SHORT).show();
        Log.i("ALARME", "O alarme executou as: "+new Date());
    }

}
