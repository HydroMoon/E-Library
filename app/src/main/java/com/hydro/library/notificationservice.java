package com.hydro.library;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class notificationservice extends Service {


    int counter = 0,notificationNumber,NotificationSetting;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int notificationNumberOfCollage;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CheckForNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class notificationCheck extends AsyncTask<Void,Void,String>{
        HttpURLConnection conn, conn2;
        String NameOfFile, subject, subtype, UserNameUploaded,SavedUserName;
        String FirstNotification = "10000";
        boolean Success = false,SuccessToDownloadFile = false;

        @Override
        protected void onPreExecute() {
            sharedPreferences  = getSharedPreferences("notification", MODE_PRIVATE);
            notificationNumber = sharedPreferences.getInt("notificationNumber", -1);
            NotificationSetting = sharedPreferences.getInt("NotificationSettingNumber", 1);
            SavedUserName = sharedPreferences.getString("UserName","UserName");
            notificationNumberOfCollage = sharedPreferences.getInt("CollageNotification",-1);//اضافة
        }

        @Override
        protected String doInBackground(Void... params) {
            editor = sharedPreferences.edit();
            try {
                URL url = new URL("http://inscrutable-sixes.000webhostapp.com/notification.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder buf = new StringBuilder();

                String str;

                while ((str = br.readLine()) != null) {
                    buf.append(str);
                }

                JSONObject json = new JSONObject(buf.toString());
                JSONArray jarr = json.getJSONArray("noti");
                JSONArray jarr1 = json.getJSONArray("Cnoti");


                if (jarr.length() != notificationNumber) {

                    if (notificationNumber == -1 || NotificationSetting == 1) {
                        notificationNumber = jarr.length() - 1;
                    }

                    JSONObject jsonObject = jarr.getJSONObject(notificationNumber);
                    String name = jsonObject.getString("name");

                    if (FirstNotification.equals(name.substring(0, name.lastIndexOf("#")))) {
                        NameOfFile = "First Notification";
                        UserNameUploaded = "E-Library";
                    } else {

                        subject = name.substring(name.lastIndexOf("$") + 1);

                        subtype = subject.substring(subject.lastIndexOf("&") + 1, subject.lastIndexOf("%"));
                        NameOfFile = subject.substring(subject.lastIndexOf("%") + 1);
                        subject = subject.substring(subject.lastIndexOf("$") + 1, subject.lastIndexOf("&"));
                        UserNameUploaded = name.substring(name.lastIndexOf("#") + 1, name.lastIndexOf("$"));

                        Success = !UserNameUploaded.equals(SavedUserName);

                    }
                    switch (NotificationSetting){

                        case 0:
                            editor.putInt("notificationNumber", notificationNumber += 1);
                            editor.apply();
                            break;

                        case 1:
                            editor.putInt("notificationNumber", jarr.length());
                            editor.apply();
                            break;
                    }
                }

                if (jarr1.length() != notificationNumberOfCollage) {//اضافة

                    JSONObject json1 = jarr1.getJSONObject(jarr1.length() - 1);

                    String LastFile = json1.getString("name");

                    if (notificationNumberOfCollage == -1) {
                        editor.putInt("CollageNotification", jarr1.length());
                        editor.apply();
                        return null;
                    } else {
//                        String notificationPlace = "/public_html/CollageNotification/" + LastFile;
                        String PathOnPhone = Environment.getExternalStorageDirectory() + File.separator + LastFile;
                       /* OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(PathOnPhone));
                        boolean SuccessToDownloadFile = ftpC.retrieveFile(notificationPlace,outputStream);
                        outputStream.close();*/
                        try {
                            URL url1 = new URL("http://inscrutable-sixes.000webhostapp.com/CollageNotification/" + LastFile);
                            conn2 = (HttpURLConnection) url1.openConnection();
                            conn2.setRequestProperty("Accept-Encoding", "identity");

                            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                return null;
                            }

                            InputStream is1 = conn2.getInputStream();

                            File file = new File(PathOnPhone);

                            OutputStream os = new FileOutputStream(file);

                            byte data[] = new byte[4096];
                            int count;
                            while ((count = is1.read(data)) != -1) {

                                os.write(data, 0, count);
                            }
                            os.flush();
                            os.close();
                            is.close();
                            SuccessToDownloadFile = true;

                        } catch (IOException ex) {
                            Log.e("ERR", ex.toString());
                        }

                        if (SuccessToDownloadFile) {
                            editor.putInt("CollageNotification", notificationNumberOfCollage + 1);
                            editor.apply();
                            return PathOnPhone;
                        } else {
                            return null;
                        }
                    }
                }

            } catch (SocketTimeoutException e) {
                Log.e("TIME_OUT", e.toString());
                return null;
            } catch (JSONException | IOException ex) {
                Log.e("JSON/IO", ex.toString());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if (Success){
                String FullPath = "http://inscrutable-sixes.000webhostapp.com/" + subject + File.separator + subtype + File.separator + NameOfFile;
                String openPath = File.separator + subject + File.separator + subtype + File.separator + NameOfFile;
                notification.notify(getBaseContext(), counter, FullPath, openPath, NameOfFile, getString(R.string.newnoti) + " (" +  NameOfFile + ") " + getString(R.string.uplodby) + " (" + UserNameUploaded + ")");
                counter++;
            }
            if (aVoid != null) {//اضافة
                notification.notify(getBaseContext(),0,aVoid,null, null,"تم رفع اشعار جديد");
            }
        }
    }

    private class checkTimer extends TimerTask {
        @Override
        public void run() {
            new notificationCheck().execute();
        }
    }

    private void CheckForNotification() {
        int checkTime = 0;
        switch (NotificationSetting) {
            case 0:
                checkTime = (1000 * 60);
                break;
            case 1:
                checkTime = (1000 * 60 * 20);
                break;
        }
        Timer timer = new Timer("My Timer",true);
        timer.scheduleAtFixedRate(new checkTimer(), 0, checkTime);
    }
}
