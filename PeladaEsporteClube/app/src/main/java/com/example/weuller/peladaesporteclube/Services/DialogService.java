package com.example.weuller.peladaesporteclube.Services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by weullermarcos on 08/09/17.
 */

public class DialogService {

    private Dialog mDialog;
    private AlertDialog alert;

    public void showProgressDialog(String message, String title, Context context) {

        hideProgressDialog();
        mDialog = ProgressDialog.show(context, title, message, false, false);
    }

    public void hideProgressDialog() {

        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    public void showAlertDialog(String message, String title, Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert = builder.create();
        alert.show();
    }

}
