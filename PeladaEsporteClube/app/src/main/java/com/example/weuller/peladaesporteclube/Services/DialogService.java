package com.example.weuller.peladaesporteclube.Services;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by weullermarcos on 08/09/17.
 */

public class DialogService {

    private Dialog mDialog;

    public void showProgressDialog(String message, String title, Context context) {

        hideProgressDialog();
        mDialog = ProgressDialog.show(context, title, message, false, false);
    }

    public void hideProgressDialog() {

        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

}
