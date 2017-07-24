package com.hydro.library;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements inter {
    public String path20, path, uploadPath, dateName;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private static final int FILE_SELECTED_CODE = 0;
    private static String uPath;
    private static String[] test;
    private File file;
    private long size_file;
    private TabLayout tab_layout;
    private ViewPager viewPager;
    private FTPClient ftpC = new FTPClient();
    private static final int result = 1;
    public String Filename, NameOfUploadedFile, openPath;
    private boolean isRunning = false, firstRun, MultiTask = false;
    private long total = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int[] tabIcons = { R.drawable.ic_cog,
            R.drawable.ic_arrow_down_bold,
            R.drawable.ic_book_open
    };
    private String Path = Environment.getExternalStorageDirectory() + File.separator;
    private EditText text;
    private int NotificationNumber, PERMISSION_ALL = 1;
    private ProgressDialog progressDialog1,progressDialog2;

    public ViewPager getPager() {
        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        CharSequence[] ListItem = new CharSequence[]{getString(R.string.showallnoti),getString(R.string.jstlastone),getString(R.string.disablenoti)};
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (id) {
            case R.id.uploadfiles:
                //uPath = new Frag0().getUploadPath();
                test = new Frag0().getUploadPath().split("<>");
                uPath = test[0];
                showFileChooser();
                break;
            case R.id.about:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.aboutus, null);
                adb.setTitle(getString(R.string.setting));
                adb.setView(view);
                adb.setIcon(R.drawable.ic_action_emo_cool);
                adb.setPositiveButton(getString(R.string.ok), null);
                adb.setNeutralButton(R.string.shareit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareApp();
                    }
                });
                adb.setNegativeButton(getString(R.string.whatsnew), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWhatsnewDialog();
                    }
                });
                adb.show();
                break;
            case R.id.exit:
                new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.exit))
                        .setMessage(getString(R.string.exitmsg))
                        .setNegativeButton(getString(R.string.no),null)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopService(new Intent(getBaseContext(),notificationservice.class));
                                finish();
                            }
                        })
                .create()
                .show();
                break;
            case R.id.notification:
                new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.noti)
                        .setSingleChoiceItems(ListItem, NotificationNumber, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                int SelectedItem = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                editor.putInt("NotificationSettingNumber", SelectedItem);
                                boolean Sucess = editor.commit();

                                NotificationNumber = sharedPreferences.getInt("NotificationSettingNumber", 1);

                                switch (NotificationNumber) {
                                    case 0:
                                    case 1:
                                        startService(new Intent(MainActivity.this, notificationservice.class));
                                        break;
                                    case 2:
                                        stopService(new Intent(MainActivity.this, notificationservice.class));
                                        break;
                                }
                                if (Sucess) {
                                    Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Not Saved Please Ask Mohand Or Gasim For This Problem", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.setUser:
                AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
                final EditText editText = new EditText(this);
                adb1.setTitle(R.string.choseuser);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint(R.string.userhint);
                adb1.setView(editText);
                adb1.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("UserName", editText.getText().toString());
                        editor.apply();
                        String test = sharedPreferences.getString("UserName", "MG");
                        Toast.makeText(MainActivity.this, test, Toast.LENGTH_SHORT).show();
                    }
                });
                adb1.show();
                break;
            case R.id.update:
                new CheckUpdate(MainActivity.this).execute();
                break;
            case R.id.connect:
                showDialog();
                //new ftpConnect().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        ApplicationInfo appInfo = getApplicationContext().getApplicationInfo();
        String path = appInfo.sourceDir;

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("*/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        startActivity(Intent.createChooser(share, "Share app via"));
    }

    private void showWhatsnewDialog () {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.whatsnew, null);

        adb.setTitle(getString(R.string.whatsnew));
        adb.setView(view);
        adb.setPositiveButton(getString(R.string.ok), null);

        adb.show();
    }
    private void showDialog () {
        final EditText jscst = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.dofanoti))
                .setView(jscst)
                .setPositiveButton(getString(R.string.snd), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(getString(R.string.dwncncl), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String jscstText = String.valueOf(jscst.getText());
                if (jscstText.length() == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.addnoti), Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    PrepareToSendCollageNotification(jscstText, MainActivity.this);
                }
            }
        });
    }


    private void PrepareToSendCollageNotification(String jscstText, Context c) {
        //progressDialog2  Is public
        progressDialog2 = ProgressDialog.show(c, null, getString(R.string.plzsndnoti), false, false);
        try {
            String PathOfFile =Environment.getExternalStorageDirectory() + File.separator +  "jscst.txt";
            File CreatePathFile = new File(PathOfFile);
            boolean SuccessToCreateFile = CreatePathFile.createNewFile();
            if (SuccessToCreateFile) {
                OutputStream outputStream = new FileOutputStream(CreatePathFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(jscstText);
                outputStreamWriter.close();
                outputStream.close();

                //path = Environment.getExternalStorageDirectory() + File.separator  Is Public
                new CheckForTheLastCollageNotification().execute(CreatePathFile.getName(), Path);
            } else {
                progressDialog2.dismiss();
                Toast.makeText(c, getString(R.string.fldtosnd), Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(c,"File Not Found",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(c,"Flied To Write",Toast.LENGTH_LONG).show();
        }
    }

    private class CheckForTheLastCollageNotification extends AsyncTask <String,Void,String> {
        String LastFile, pathOfNotification  = "/public_html/CollageNotification";
        @Override
        protected String doInBackground(String... params) {
            ftpC.enterLocalPassiveMode();
            try {
                ftpC.setFileType(FTP.BINARY_FILE_TYPE);
                FTPFile[] ftpFiles = ftpC.listFiles(pathOfNotification);

                LastFile = ftpFiles[ftpFiles.length - 1].getName();
                LastFile = LastFile.substring(0,LastFile.lastIndexOf("-"));
                int NumberOfLastFile = Integer.parseInt(LastFile) + 1;
                String NameOfNotificationFile = NumberOfLastFile + "-" + params[0];

                File from = new File(params[1], params[0]);
                File to = new File(params[1], NameOfNotificationFile);
                if (from.exists()) {
                    boolean Rename = from.renameTo(to);
                    if (Rename) {
                        InputStream i = new FileInputStream(to.getAbsoluteFile());
                        pathOfNotification+=File.separator + to.getName();
                        boolean SuccessToSendFile = ftpC.storeFile(pathOfNotification, i);
                        i.close();
                        if (SuccessToSendFile) {
                            boolean del = to.delete();
                            if (del) {
                                return getString(R.string.notisndcmp);
                            }else
                                return getString(R.string.notisndcmp) + " " + getString(R.string.fldtodeltempfile);
                        } else
                            return getString(R.string.sndfld);
                    } else {
                        return getString(R.string.errrnamenoti);
                    }
                } else
                    return getString(R.string.filentfound);

            } catch (IOException e) {
                new File(params[1] + params[0]).delete();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog2.dismiss();
            if (s == null) {
                Toast.makeText(getApplicationContext(), R.string.fldtochcknoti, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void sData(ArrayList<listItem> data, String upPath) {
        String tag = "android:switcher:" + R.id.viewpager + ":" + 1;
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        Frag1 frag = (Frag1) f;
        frag.updateR(data);
        uploadPath = upPath;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("notification", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        NotificationNumber  = sharedPreferences.getInt("NotificationSettingNumber",1);

        firstRun  = sharedPreferences.getBoolean("FirstRun", true);

        if (NotificationNumber == 2)
            stopService(new Intent(MainActivity.this, notificationservice.class));
        else
            startService(new Intent(MainActivity.this, notificationservice.class));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            createDirs();
        }

        permission_check();

        if (firstRun) {

            showWhatsnewDialog();
            final AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
            final EditText editText = new EditText(this);
            adb1.setTitle(R.string.welcometit);
            adb1.setMessage(R.string.welcomemsg);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setHint(getString(R.string.userhint));
            adb1.setView(editText);
            adb1.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String GetUserName = String.valueOf(editText.getText());
                    editor.putString("UserName", editText.getText().toString());

                    if (editText.length() == 0) {
                        Toast.makeText(getBaseContext(), R.string.welcometoast, Toast.LENGTH_LONG).show();
                        editor.putString("UserName", "UserName");
                        editor.putBoolean("FirstRun", false);
                        editor.apply();
                    } else {
                        editor.putString("UserName", GetUserName);
                        editor.putBoolean("FirstRun", false);
                        editor.apply();
                        Toast toast =  Toast.makeText(MainActivity.this, getString(R.string.welcom) + " " + GetUserName, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    new ftpConnect().execute();
                }
            });
            adb1.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopService(new Intent(MainActivity.this, notificationservice.class));
                    finish();
                }
            });
            adb1.show();

        } else {
            new ftpConnect().execute();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        tab_layout = (TabLayout) findViewById(R.id.tabs);
        tab_layout.setupWithViewPager(viewPager);
        setupTabIcons();
        if (getResources().getBoolean(R.bool.is_right_to_left)) {
            viewPager.setRotationY(180);
        }


        onNewIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        String FullPath;
        if (extras != null) {


            if (extras.containsKey("FileName")) {
                if (extras.getString("FileName") != null) { //اضافة
                NameOfUploadedFile = extras.getString("FileName");

                if (extras.containsKey("path")) {
                    FullPath = extras.getString("path");
                    openPath = extras.getString("openPath");
                    openPath.replace(" ", "%20");
                    Log.i("1", FullPath);
                    Log.i("2", openPath);
                    Log.i("3", NameOfUploadedFile);
                    new notificationDownloader().execute(FullPath);
                }
            }

                if (extras.getString("FileName") == null) {//اضافة
                    if (extras.containsKey("path")) {//اضافة
                        ReadTheFile(this, extras.getString("path"));//اضافة
                    }

                }
            }

            super.onNewIntent(intent);
        }
    }
    public void  ReadTheFile(Context c, String aVoid) {//اضافة
        String ret = "";
        File Path = new File(aVoid);
        try {
            if (Path.exists()) {
                InputStream is = new FileInputStream(Path);
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                is.close();
                boolean del = Path.delete();
                ret = stringBuilder.toString();
                new AlertDialog.Builder(c)
                        .setMessage(ret)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
                if (!del) {
                    Toast.makeText(c, "File Not Delete Please It Manual", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(c,"Flied To Read The File",Toast.LENGTH_LONG).show();
        }
        //نهاية اشعارات الدفعة
    }

    class notificationDownloader extends AsyncTask <String,Integer,String>{
        ProgressDialog progressDialog;
        HttpURLConnection Conn;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getString(R.string.downloading));
            progressDialog.setMessage(NameOfUploadedFile + " " + getString(R.string.notidialog));
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIcon(R.drawable.happy);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String PathOnPhone = Environment.getExternalStorageDirectory() + File.separator + "SEL" + openPath;

                URL url = new URL(params[0]);
                Conn = (HttpURLConnection) url.openConnection();
                Conn.setConnectTimeout(10000);
                Conn.setReadTimeout(10000);

                Conn.connect();

                if (Conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return Conn.getResponseCode() + " " + Conn.getResponseMessage();
                }

                int fileSize = Conn.getContentLength();

                InputStream is = Conn.getInputStream();
                File file = new File(PathOnPhone);
                OutputStream os = new FileOutputStream(file);


                byte[] data = new byte[4096];
                long total = 0;
                int count;



                while ((count = is.read(data)) !=-1){
                    total += count;

                    if (fileSize > 0) {
                        publishProgress((int) (total * 100 / fileSize));
                    }

                    os.write(data,0,count);
                }
                os.flush();
                os.close();
                is.close();

                return "Successfully Downloaded The File";
            } catch (SocketTimeoutException e) {
                Log.i("EXCEP..", e.toString());
                return "Flied To Download The File Ask Mohand Or Gasim";
            } catch (IOException ex) {
                Log.i("EXCEP..", ex.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.dwncmp))
                    .setMessage(getString(R.string.opnfile) + " " + NameOfUploadedFile)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getBaseContext(), "Just Give It Path", Toast.LENGTH_LONG).show();
                            String filePath = Environment.getExternalStorageDirectory() + File.separator + "SEL" + openPath;
                            File file = new File(filePath);

                            Uri path = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            MimeTypeMap typeMap = MimeTypeMap.getSingleton();

                            if (fileEx(NameOfUploadedFile) != null) {
                                String mime_type = typeMap.getMimeTypeFromExtension(fileEx(NameOfUploadedFile));
                                intent.setDataAndType(path, mime_type);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(MainActivity.this, getString(R.string.erroccur), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .create()
                    .show();
        }
    }

    class sendNotification extends AsyncTask<Integer, Void, Integer> {

        String LastNotificationFile;
        int NumberOfLastNotificationFile;
        boolean SuccessToCreateTextNotification;
        String UserName = sharedPreferences.getString("UserName", "MG");

        @Override
        protected Integer doInBackground(Integer... params) {

            ftpC.enterLocalPassiveMode();
            try {
                ftpC.setFileType(FTP.BINARY_FILE_TYPE);
                FTPFile[] files = ftpC.listFiles("/public_html/notification");

                LastNotificationFile = files[files.length - 1].getName();
                LastNotificationFile = LastNotificationFile.substring(0, LastNotificationFile.lastIndexOf("#"));

                NumberOfLastNotificationFile  = Integer.parseInt(LastNotificationFile);
                NumberOfLastNotificationFile+=1;

            } catch (IOException e) {
                return null;
            }
            return  NumberOfLastNotificationFile;
        }

        @Override
        protected void onPostExecute(Integer s) {

            if (s == null) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Flied to send notification file")
                        .setMessage("Flied to check number of last notification file please try again or no one will receive your file")
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new sendNotification().execute();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Flied To Send Notification File", Toast.LENGTH_LONG).show();
                            }
                        })
                        .create()
                        .show();
            } else {
                dateName = NumberOfLastNotificationFile + "#" + UserName + "$" + test[1] + "%" + Filename;
                File root = new File(Environment.getExternalStorageDirectory() + File.separator + dateName);
                if (!root.exists()) {
                    try {
                        SuccessToCreateTextNotification = root.createNewFile();
                        if (SuccessToCreateTextNotification) {

                                new notiUpload().execute(root);
                        } else {
                            Toast.makeText(getBaseContext(), "Flied To  Created Notification File", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class notiUpload extends AsyncTask<File, Void, String> {
        String pathOfNotification = "/public_html/notification/" + dateName;
        boolean success = false;

        @Override
        protected String doInBackground(File... params) {
            ftpC.enterLocalPassiveMode();
            try {
                ftpC.setFileType(FTP.BINARY_FILE_TYPE);
                InputStream i = new FileInputStream(params[0]);
                ftpC.storeFile(pathOfNotification, i);
                i.close();
                success = true;
                return "Success To Send Notification text";
            } catch (IOException e) {
                return "Filed To Send Notification text";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (success) {
                File FilePath = new File(Environment.getExternalStorageDirectory() + File.separator);
                File DF = new File(FilePath, dateName);
                boolean d = DF.delete();
                if (!d) {
                    Toast.makeText(getBaseContext(), "Flied To Delete " + dateName + " Please Deleted ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String fileEx(String uri){
        if (uri.contains("?")){
            uri = uri.substring(0,uri.indexOf("?"));
        }
        if (uri.lastIndexOf(".")==-1){
            return null;
        }else{
            String ext = uri.substring(uri.lastIndexOf(".") + 1);

            if (ext.contains("%")){
                ext = ext.substring(0,ext.indexOf("%"));
            }
            if (ext.contains("/")){
                ext  = ext.substring(0,ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    private void permission_check() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            permission_check();
            createDirs();
        } else {
            createDirs();
        }
    }

    private  void setupTabIcons() {
        tab_layout.getTabAt(0).setIcon(tabIcons[0]);
        tab_layout.getTabAt(1).setIcon(tabIcons[1]);
        tab_layout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Frag0(), "");
        adapter.addFragment(new Frag1(), "");
        adapter.addFragment(new Frag2(), "");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFL = new ArrayList<>();
        private final List<String> mFTL = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFL.get(position);
        }

        @Override
        public int getCount() {
            return mFL.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFL.add(fragment);
            mFTL.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void showFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setPackage("com.estrongs.android.pop.pro");


        try {
            startActivityForResult(intent, FILE_SELECTED_CODE);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage("com.estrongs.android.pop");

            try {
                startActivityForResult(intent, FILE_SELECTED_CODE);
            } catch (ActivityNotFoundException e) {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.appismissing));
                adb.setMessage(R.string.esexp);
                adb.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Handler handler = new Handler();
                        final ProgressDialog mPD = new ProgressDialog(MainActivity.this);
                        final ConnectInternet connTest = new ConnectInternet(MainActivity.this);
                        mPD.setMessage(getString(R.string.initiatedownload));
                        mPD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mPD.setCancelable(false);
                        mPD.setCanceledOnTouchOutside(false);
                        mPD.setButton(-1, getString(R.string.cancBtn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isRunning = true;
                            }
                        });
                        mPD.setMax(100);

                        final Thread downloadES = new Thread(new Runnable() {
                            InputStream inputStream;
                            OutputStream outputStream;
                            File ES_dir;
                            String ESpath = Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + "ES.apk";

                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPD.show();
                                    }
                                });
                                while (!isRunning) {
                                    try {
                                        if (connTest.is_connected()) {
                                            HttpURLConnection es;
                                            URL url = new URL("http://gasim.000webhostapp.com/ES.apk");
                                            es = (HttpURLConnection) url.openConnection();
                                            es.connect();

                                            if (es.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                                isRunning = true;
                                            }

                                            final int esSize = es.getContentLength();

                                            inputStream = es.getInputStream();
                                            ES_dir = new File(ESpath);

                                            if (ES_dir.exists() && ES_dir.length() == esSize) {
                                                inputStream.close();
                                                isRunning = true;
                                            }

                                            outputStream = new FileOutputStream(ES_dir);

                                            byte data[] = new byte[4096];

                                            int count;

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mPD.setMessage(getString(R.string.downloading));
                                                    mPD.setMax(100);
                                                }
                                            });

                                            while ((count = inputStream.read(data)) != -1) {
                                                total += count;
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mPD.setProgress((int) (total * 100) / esSize);
                                                    }
                                                });
                                                //ES_dir.delete();
                                                outputStream.write(data, 0, count);
                                            }


                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mPD.dismiss();
                                                }
                                            });

                                            outputStream.flush();
                                            outputStream.close();
                                            inputStream.close();
                                            isRunning = true;
                                        }

                                    } catch (IOException e) {
                                        isRunning = true;
                                        Log.e("ES", e.toString());
                                    } finally {
                                        try {
                                            Intent install = new Intent();
                                            install.setDataAndType(Uri.fromFile(ES_dir), "application/vnd.android.package-archive");
                                            startActivity(install);

                                        } catch (ActivityNotFoundException ex) {

                                        }
                                    }
                                }
                            }
                        });
                        downloadES.start();
                    }
                }).setNegativeButton(getString(R.string.no), null);
                adb.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECTED_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        path20 = FileUtils.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (path20 == null) {
                        Toast.makeText(getApplicationContext(), "You Can't Upload (Video Or Image Or Audio)", Toast.LENGTH_LONG).show();
                    }else{
                        file = new File(path20);
                        Filename = file.getName();
                        size_file = file.length();
                        AlertDialog.Builder adb  =new AlertDialog.Builder(this);
                        adb.setTitle("Uploading Local File");
                        adb.setMessage("Do You Want To Upload " + file.getName() + " ?");
                        adb.setNegativeButton("No", null);
                        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (ftpC.isConnected()) {
                                    new CheckIfFileHasBeenUploaded().execute(file);
                                } else {
                                   MultiTask = true;
                                    new ftpConnect().execute();
                                }
                            }
                        });
                        adb.setNeutralButton("rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                renameFiles();
                            }
                        });
                        adb.show();
                    }
                }
                break;
            case result:
                if (resultCode == RESULT_OK) {
                    String newfilename = data.getExtras().getString("FileName");
                    String filetype = file.getName();
                    filetype = filetype.substring(filetype.lastIndexOf("."));
                    newfilename += filetype;
                    String oldfiename = path20.substring(path20.lastIndexOf("/"));
                    oldfiename = oldfiename.substring(1);
                    String newpath = path20.substring(0, path20.lastIndexOf("/"));
                    newpath = newpath.substring(1) + File.separator;
                    File dir = new File(newpath);
                    if (dir.exists()) {
                        File from = new File(newpath, oldfiename);
                        File to = new File(newpath, newfilename);
                        if (from.exists()) {
                            boolean success = from.renameTo(to);
                            if (success) {
                                Toast.makeText(MainActivity.this, "File has been renamed from " + from.getName() + " to " + to.getName(), Toast.LENGTH_LONG).show();
                                Filename = to.getName();
                                size_file = to.length();
                                new ftpUpload().execute(to.getAbsolutePath());
                            } else {
                                Toast.makeText(MainActivity.this, "Cant rename file on sd card", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "File path does not exist", Toast.LENGTH_LONG).show();

                    }
                } else if (resultCode == RESULT_CANCELED) {
                    new CheckIfFileHasBeenUploaded().execute(file);
                }
                break;
        }
    }

    private void renameFiles () {
        final EditText rename = new EditText(MainActivity.this);
        rename.setInputType(InputType.TYPE_CLASS_TEXT);
        rename.setHint(getString(R.string.hintrename));
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Rename " + Filename)
                .setView(rename)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newfilename = String.valueOf(rename.getText());
                        String filetype = file.getName();

                        if (!filetype.contains(".")) {
                            Toast.makeText(MainActivity.this, "File has no extension or type", Toast.LENGTH_LONG).show();
                        } else {
                            filetype = filetype.substring(filetype.lastIndexOf("."));
                            newfilename += filetype;
                        String oldfiename = path20.substring(path20.lastIndexOf("/"));
                        oldfiename = oldfiename.substring(1);
                        String newpath = path20.substring(0, path20.lastIndexOf("/"));
                        newpath = newpath.substring(1) + File.separator;
                        File dir = new File(newpath);
                        if (dir.exists()) {
                            File from = new File(newpath, oldfiename);
                            File to = new File(newpath, newfilename);
                            if (from.exists()) {
                                boolean success = from.renameTo(to);
                                if (success) {
                                    Toast.makeText(MainActivity.this, "File has been renamed from " + from.getName() + " to " + to.getName(), Toast.LENGTH_LONG).show();
                                    Filename = to.getName();
                                    size_file = to.length();
                                    new ftpUpload().execute(to.getAbsolutePath());
                                } else {
                                    Toast.makeText(MainActivity.this, "Cant rename file on sd card", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "File path does not exist", Toast.LENGTH_LONG).show();

                        }
                    }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CheckIfFileHasBeenUploaded().execute(file);
                    }
                })
                .create()
                .show();
    }

    class CheckIfFileHasBeenUploaded extends AsyncTask<File,Void,File>{

        boolean SuccessToFindFile;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext(),"Please Wait We Are Checking If The File Has Been Upload Before",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected File doInBackground(File... params) {

            try {
                FTPFile[] ftpFiles = ftpC.listFiles("/public_html" + uPath);

                for (FTPFile file : ftpFiles){

                    if (file.getName().contains(Filename)){
                        SuccessToFindFile = true;
                        break;
                    }
                }
            } catch (IOException e) {
                SuccessToFindFile = false;
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(File s) {
            if (SuccessToFindFile){
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Uploading")
                        .setMessage("This File " + Filename + " Has Been Already Uploaded Do You Want To Rename It ?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "No Files Has Been Uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                renameFiles();
                            }
                        });

                adb.show();

            }else {
                new ftpUpload().execute(s.getAbsolutePath());
            }
        }
    }


    class ftpConnect extends AsyncTask<Void,String,FTPClient> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected FTPClient doInBackground(Void... params) {
            try {
                String ser = "files.000webhost.com";
                int port = 21;
                ftpC.setControlEncoding("UTF-8");
                ftpC.connect(ser,port);
                if (ftpC.isConnected()){
                    publishProgress("Connected To server");
                }
                String username = "inscrutable-sixes";
                String pass = "999";
                boolean success = ftpC.login(username,pass);
                ftpC.setFileType(FTP.BINARY_FILE_TYPE);
                ftpC.enterLocalPassiveMode();
                if (success){
                    publishProgress("Success To Login");
                }
            } catch (IOException e) {
                publishProgress("Flied To Connect Please Try Again");
            }
            return ftpC;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getBaseContext(),values[0],Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(FTPClient s) {
            super.onPostExecute(s);
            if (s.isConnected() && MultiTask) {
                MultiTask = false;
                new CheckIfFileHasBeenUploaded().execute(file);
            }
            Adapter ada = new Adapter();
            ada.setFTP(ftpC);
        }
    }

    class ftpUpload extends AsyncTask<String,Integer,String> {
        private boolean success;
        ProgressDialog progressDialog1 = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.setMessage(getString(R.string.sending) + " " + Filename);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(-1, getString(R.string.cancBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

        }

        @Override
        protected String doInBackground(String... params) {
                path = "/public_html" + uPath + Filename;
            try {
                    ftpC.enterLocalPassiveMode();
                    ftpC.setFileType(FTP.BINARY_FILE_TYPE);
                    InputStream i = new BufferedInputStream(new FileInputStream(params[0]));
                    OutputStream o = ftpC.storeFileStream(path);
                    byte[] BA = new byte[8192];
                    float total = 0;
                    int read;

                    while (((read = i.read(BA)) != -1 && !isCancelled())) {
                        total += read;
                        o.write(BA, 0, read);
                        int sum = (int) ((total / size_file) * 100);
                        publishProgress(sum);
                    }
                    o.flush();
                    o.close();
                    i.close();
                    success = ftpC.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (success) {
                return "Successfully Uploading File";
            }else {
                return params[0];
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(final String aVoid) {
            progressDialog.dismiss();
            if (!success){
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Reconnect");
                adb.setMessage("Do You Want To Reconnecting To Server ?");
                adb.setNegativeButton("No", null);
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MultiTask =true;
                        new ftpConnect().execute();
                    }
                });
                adb.show();
            }else {
                Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_SHORT).show();
                    new sendNotification().execute();
            }
        }
    }

    public void createDirs() {
        String[] folders = new String[] {"OS", "DB", "CG", "NET", "OOAD", "OOP", "SER", "UPDATE"};
        String[] subfolder = new String[] {"M", "L", "E", "O"};
        File mDir = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL");
        if (mDir.exists())
            Log.i("","");
        else
            mDir.mkdir();

        for (String folder : folders) {
            File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + folder);
            if (dir.exists())
                Log.i("", "");
            else
                dir.mkdir();
        }
        for (int x = 0 ; x < folders.length; x++) {

            for (int i = 0; i < subfolder.length; i ++) {
                File sdir = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + folders[x] + File.separator + subfolder[i]);
                if (sdir.exists())
                    Log.i("", "");
                else
                    sdir.mkdir();
            }
        }
    }

    private class CheckUpdate extends AsyncTask<String, Void, String> {
        PackageInfo packageInfo;
        private HttpURLConnection conn;
        private URL url;
        private Context context;
        private BufferedReader bufferedReader;
        private ProgressDialog mPD;

        CheckUpdate(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPD = new ProgressDialog(context);
            mPD.setMessage(context.getString(R.string.checking));
            mPD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPD.setCancelable(false);
            mPD.setCanceledOnTouchOutside(false);
            mPD.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                packageInfo = getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException Ne) {

            }
            try {
                int curVersion = packageInfo.versionCode;

                url = new URL("http://gasim.000webhostapp.com/UPDATE/c.txt");
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "err";
                }

                InputStream is = conn.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder StBuffer = new StringBuilder();
                String update;

                while ((update = bufferedReader.readLine()) != null) {
                    StBuffer.append(update);
                }
                bufferedReader.close();
                is.close();
                conn.disconnect();

                if (Integer.parseInt(StBuffer.toString()) != curVersion) {
                    return "found";
                } else {
                    return "notfound";
                }
            } catch (IOException e) {
                return "err";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mPD.dismiss();
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setCancelable(false);
            switch (s) {
                case "found":
                    adb.setTitle(context.getString(R.string.update));
                    adb.setIcon(R.drawable.happy);
                    adb.setMessage(getString(R.string.updatefound));
                    adb.setPositiveButton(getString(R.string.downbtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DownUpdate(context).execute();
                        }
                    });
                    adb.setNegativeButton(getString(R.string.cancBtn), null);
                    adb.show();
                    break;
                case "notfound":
                    adb.setMessage(getString(R.string.noupdate));
                    adb.setIcon(R.drawable.ic_check);
                    adb.setPositiveButton(getString(R.string.ok), null);
                    adb.show();
                    break;
                case "err":
                    Toast.makeText(context, context.getString(R.string.httperr), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class DownUpdate extends AsyncTask<String, Integer, String> {
        private PowerManager.WakeLock mWL;
        private HttpURLConnection conn;
        private URL url;
        private Context context;
        private InputStream appis;
        private OutputStream appos;
        private File appUp;
        private ProgressDialog mPD;
        private String urlPath = "http://gasim.000webhostapp.com/UPDATE/APP_DEV.apk";

        DownUpdate(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWL = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWL.acquire();
            mPD = new ProgressDialog(context);
            mPD.setMessage(context.getString(R.string.downloading));
            mPD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPD.setCancelable(true);
            mPD.setCanceledOnTouchOutside(false);
            mPD.setButton(-1, context.getString(R.string.cancBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(urlPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return context.getString(R.string.httperr);
                }

                int fileSize = conn.getContentLength();

                appis = conn.getInputStream();
                appUp = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + "UPDATE" + File.separator + "UPDATE.apk");
                if (appUp.exists() && appUp.length() == fileSize) {
                    return "1";
                } else {
                    publishProgress(0);
                    appos = new FileOutputStream(appUp);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;

                    while ((count = appis.read(data)) != -1) {
                        total += count;
                        if (fileSize > 0) {
                            publishProgress((int) (total * 100 / fileSize));
                        }
                        appos.write(data, 0, count);
                    }
                    appos.flush();
                    appos.close();
                    appis.close();
                    return "2";
                }
            } catch (IOException ex) {
                appUp.delete();
                return context.getString(R.string.dwnfld);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
                mPD.setMax(100);
                mPD.setProgress(values[0]);
                mPD.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mWL.release();
            mPD.dismiss();
            ADB(s);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            mWL.release();
            mPD.dismiss();
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }

        private void ADB(String result) {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setCancelable(false);
            adb.setTitle(context.getString(R.string.update));
            if (result == "2") {
                //adb.setIcon();
                adb.setMessage(R.string.updtcmp);
                adb.setPositiveButton(getString(R.string.install), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent installApk = new Intent(Intent.ACTION_VIEW);
                            installApk.setDataAndType(Uri.fromFile(appUp), "application/vnd.android.package-archive");
                            context.startActivity(installApk);
                        }catch (ActivityNotFoundException ex){
                            Toast.makeText(context, context.getString(R.string.erroccur), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                adb.setNegativeButton(getString(R.string.no), null);
                adb.show();
            } else if (result == "1") {
                adb.setMessage(R.string.latestversion);
                adb.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent installApk = new Intent(Intent.ACTION_VIEW);
                            installApk.setDataAndType(Uri.fromFile(appUp), "application/vnd.android.package-archive");
                            context.startActivity(installApk);
                        }catch (ActivityNotFoundException ex){
                            Toast.makeText(context, getString(R.string.err) + context.getString(R.string.erroccur), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                adb.setNegativeButton(context.getString(R.string.cancBtn), null);
                adb.show();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }

        }
    }
}
