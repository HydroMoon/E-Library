package com.hydro.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;

public class NotificationBrodcastRecevie extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getStringExtra("FilePath");

        if (action.equals("Canceled")) {
            boolean del = new File(path).delete();
            if (del) {
                Toast.makeText(context, context.getString(R.string.notidelsucced), Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(context, context.getString(R.string.fldtodelnoti), Toast.LENGTH_LONG).show();
        }
    }
}
