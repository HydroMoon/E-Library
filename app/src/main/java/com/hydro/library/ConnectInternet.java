package com.hydro.library;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mohand on 08/06/2017.
 */

class ConnectInternet {
    private Context context;
    ConnectInternet(Context context) {
        this.context = context;
    }

    boolean is_connected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info!=null){
                if (info.getState()==NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
