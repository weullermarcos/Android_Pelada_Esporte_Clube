package com.example.weuller.peladaesporteclube.Receivers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by weuller on 27/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private AlertDialog alert;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Disparou o Alarme", Toast.LENGTH_SHORT).show();
        Log.i("ALARME", "O alarme executou as: "+new Date());

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Atenção");
//        builder.setMessage("O seu futebol marcado começará daqui uma hora");
//        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        alert = builder.create();
//        alert.show();
    }
}
