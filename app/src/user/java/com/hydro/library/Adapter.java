package com.hydro.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private ArrayList<listItem> items;
    private String fName;
    private String path = "http://inscrutable-sixes.000webhostapp.com";
    private ArrayList<Integer> posimg = new ArrayList<>();
    private static String DPath, sPath;
    private boolean finished = true;


    Adapter() {
    }


    void download(String Dpath, String savePath) {
        DPath = Dpath;
        sPath = savePath;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, size;
        Button[] btn = new Button[2];
        ImageView icon;
        ProgressBar progressBar;


        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.namee);
            size = (TextView) view.findViewById(R.id.listsize);
            btn[0] = (Button) view.findViewById(R.id.dBtn);
            btn[1] = (Button) view.findViewById(R.id.cBtn);
            icon = (ImageView) view.findViewById(R.id.imgview);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            btn[0].setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {

            fName = items.get(getAdapterPosition()).getNameF();
            path += DPath + fName;

            if (finished) {
                new DownloadTask(v.getContext(), progressBar, btn).execute(path);
            } else {
                Snackbar.make(v, "task not finished", Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    Adapter(ArrayList<listItem> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adapter.MyViewHolder holder, int position) {
        holder.name.setText(items.get(position).getNameF());
        holder.size.setText(items.get(position).getItemF());



        if (items.get(position).getNameF().endsWith(".docx")){
            posimg.add(R.drawable.docx_win);
        }else if (items.get(position).getNameF().endsWith(".pdf")){
            posimg.add(R.drawable.pdf);
        }else if (items.get(position).getNameF().endsWith(".pptx")){
            posimg.add(R.drawable.pptx_win);
        }else if (items.get(position).getNameF().endsWith(".rar")){
            posimg.add(R.drawable.rar);
        }else if (items.get(position).getNameF().endsWith(".txt")){
            posimg.add(R.drawable.text);
        }else if (items.get(position).getNameF().endsWith(".zip")){
            posimg.add(R.drawable.zip);
        }else {
            posimg.add(R.drawable.unknown);
        }

        holder.icon.setImageResource(posimg.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWL;
        private File file;
        private ProgressBar progressBar;
        private Button[] btn = new Button[2];

        DownloadTask(Context context, ProgressBar progressBar, Button[] btn) {
            this.context = context;
            this.progressBar = progressBar;
            this.btn = btn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + sPath + fName);
            //prevent power button from interrupting download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWL = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWL.acquire();
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            btn[0].setVisibility(View.GONE);
            btn[1].setVisibility(View.VISIBLE);
            btn[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel(true);
                }
            });
        }

        @Override
        protected String doInBackground(String... sUrl) {
            finished = false;
            InputStream is;
            OutputStream os;
            HttpURLConnection conn = null;


                try {
                    URL url = new URL(sUrl[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    //conn.connect();


                    //wait for server response 200 so we not get a wrong file
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return context.getString(R.string.httperr);
                    }

                    //file size
                    int fileLength = conn.getContentLength();


                    //Dzownload the file
                    is = conn.getInputStream();

                    if (file.length() == fileLength) {
                        return context.getString(R.string.fileExist);
                    } else {
                        os = new FileOutputStream(file);

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = is.read(data)) != -1) {

                            total += count;
                            //increasing the progressbar
                            publishProgress((int) (100 * total) / fileLength);
                            os.write(data, 0, count);
                        }
                        os.flush();
                        os.close();
                        is.close();
                        return context.getString(R.string.dwncmp);
                    }

                } catch (IOException e) {

                    if (isCancelled()) {
                        return context.getString(R.string.dwncncl);
                    } else {
                        return context.getString(R.string.dwnfld);
                    }
                } finally {
                    conn.disconnect();
                }

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            mWL.release();
            progressBar.setVisibility(View.GONE);
            btn[1].setVisibility(View.GONE);
            btn[0].setVisibility(View.VISIBLE);

            if (s == context.getString(R.string.dwnfld) || s == context.getString(R.string.dwncncl)) {
                if (file.exists()) {
                    file.delete();
                }
            }

            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

            path = "http://inscrutable-sixes.000webhostapp.com";

            if (s.equals(context.getString(R.string.dwncmp))) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.dwncmp))
                        .setMessage(context.getString(R.string.opnfile) + " " + fName)
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri path;
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                    path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                                } else {
                                    path = Uri.fromFile(file);
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                MimeTypeMap typeMap = MimeTypeMap.getSingleton();
                                String mime_type = typeMap.getMimeTypeFromExtension(fileEx(fName));
                                intent.setDataAndType(path, mime_type);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {

                                    context.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(context, R.string.noapp, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), null)
                        .create()
                        .show();

            }
            finished = true;
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

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            if(s == context.getString(R.string.dwncncl)) {
                if (file.exists()) {
                    file.delete();
                }
            }
            mWL.release();
            path = "http://inscrutable-sixes.000webhostapp.com";
            progressBar.setVisibility(View.GONE);
            btn[1].setVisibility(View.GONE);
            btn[0].setVisibility(View.VISIBLE);
            finished = true;
            Toast.makeText(context, context.getString(R.string.err) + " " + s, Toast.LENGTH_LONG).show();
        }
    }

}

