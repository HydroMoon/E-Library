package com.hydro.library;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


class Adapter2 extends RecyclerView.Adapter<Adapter2.MyViewHolder> {

    private ArrayList<listItem> items;
    private ArrayList<Integer> position_image = new ArrayList<>();
    private static String Path;
    private String nameOfAPK, urlForApk;
    private TextView notfound;

    Adapter2() {
    }

    Adapter2(int x) {
    }

    void setTxt(TextView txt) {
        this.notfound = txt;
    }

    void setPath(String path) {
        Path = path;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView name;
        ImageView icon;
        TextView size;
        Button share;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.filename);
            size = view.findViewById(R.id.filesize);
            icon = view.findViewById(R.id.listfiles);
            share = view.findViewById(R.id.shareBtn);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shared_path = items.get(getAdapterPosition()).getNameF();
                    File shared_file_path = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator+ Path + File.separator + shared_path);

                    Intent intent_shared_file = new Intent(Intent.ACTION_SEND);
                    intent_shared_file.setType("application/pdf");
                    intent_shared_file.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + shared_file_path));
                    intent_shared_file.putExtra(Intent.EXTRA_SUBJECT,"sharing File");
                    intent_shared_file.putExtra(Intent.EXTRA_TEXT,"sharing File");
                    v.getContext().startActivity(Intent.createChooser(intent_shared_file, "share File"));
                }
            });
        }

        @Override
        public void onClick(final View v) {
            String File_name  = items.get(getAdapterPosition()).getNameF();
            File ex_Store = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + Path + File.separator + File_name);
            Uri path;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                path = FileProvider.getUriForFile(v.getContext(), BuildConfig.APPLICATION_ID + ".provider", ex_Store);
            } else {
                path = Uri.fromFile(ex_Store);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            MimeTypeMap typeMap = MimeTypeMap.getSingleton();

            if (fileEx(File_name) != null) {
                String mime_type = typeMap.getMimeTypeFromExtension(fileEx(File_name));
                intent.setDataAndType(path, mime_type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(v.getContext().getString(R.string.appismissing)).setMessage(v.getContext().getString(R.string.missingapp));
                    builder.setIcon(R.drawable.ic_action_emo_cry);
                    builder.setNegativeButton(v.getContext().getString(R.string.no), null);
                    builder.setNeutralButton(v.getContext().getString(R.string.gotomar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String extension = "";
                            if (items.get(getAdapterPosition()).getNameF().endsWith(".pptx"))
                            {
                                extension = "pptx";
                            }
                            else if (items.get(getAdapterPosition()).getNameF().endsWith(".docx"))
                            {
                                extension = "docx";
                            }
                            else if (items.get(getAdapterPosition()).getNameF().endsWith(".pdf"))
                            {
                                extension = "pdf";
                            }

                            Intent goToMarket = new Intent(Intent.ACTION_VIEW);
                            goToMarket.setData(Uri.parse("market://search?q=" + extension));
                            try {
                                v.getContext().startActivity(goToMarket);
                            } catch (ActivityNotFoundException ex) {
                                Log.i("","");
                            }
                        }
                    });
                    builder.setPositiveButton(v.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (items.get(getAdapterPosition()).getNameF().endsWith(".docx")){
                                nameOfAPK = "WPS_Office.apk";
                                urlForApk = "http://gasim.000webhostapp.com/WPS_Office.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else if (items.get(getAdapterPosition()).getNameF().endsWith(".pdf")){
                                nameOfAPK = "WPS_Office.apk";
                                urlForApk = "http://gasim.000webhostapp.com/WPS_Office.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else if (items.get(getAdapterPosition()).getNameF().endsWith(".pptx")){
                                nameOfAPK = "WPS_Office.apk";
                                urlForApk = "http://gasim.000webhostapp.com/WPS_Office.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else if (items.get(getAdapterPosition()).getNameF().endsWith(".rar")){
                                nameOfAPK = "Es_Explorer.apk";
                                urlForApk = "http://gasim.000webhostapp.com/Es_Explorer.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else if (items.get(getAdapterPosition()).getNameF().endsWith(".txt")){
                                nameOfAPK = "ColorNote.apk";
                                urlForApk = "http://gasim.000webhostapp.com/ColorNote.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else if (items.get(getAdapterPosition()).getNameF().endsWith(".zip")){
                                nameOfAPK = "Es_Explorer.apk";
                                urlForApk = "http://gasim.000webhostapp.com/Es_Explorer.apk";
                                new DownMissingApps(v.getContext()).execute(urlForApk);
                            }else {
                                Toast.makeText(v.getContext(), v.getContext().getString(R.string.unknownexten), Toast.LENGTH_LONG).show();
                            }
                            
                        }
                    });
                    builder.show();
                }
            } else {
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.erroccur), Toast.LENGTH_SHORT).show();
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

        @Override
        public boolean onLongClick(final View v) {
            final String fileName = items.get(getAdapterPosition()).getNameF();
            if (items.size() != 0) {
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(v.getContext());
                adb.setTitle(v.getContext().getString(R.string.delmsg));
                adb.setIcon(R.drawable.ic_delete);
                adb.setMessage(v.getContext().getString(R.string.delDialog) + " " + fileName);
                adb.setNegativeButton(v.getContext().getString(R.string.cancBtn), null);
                adb.setPositiveButton(v.getContext().getString(R.string.delBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + Path + File.separator);
                        File FD = new File(f, fileName);
                        boolean deleted = FD.delete();
                        if (deleted) {
                            items.remove(items.get(getAdapterPosition()));
                            position_image.remove(getAdapterPosition());
                            notifyDataSetChanged();

                            if (getItemCount() == 0) {
                                notfound.setVisibility(View.VISIBLE);
                            }else
                                notfound.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.delfailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adb.show();
            }
            return true;
        }
    }



    Adapter2(ArrayList<listItem> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adapter2.MyViewHolder holder, int position) {
        holder.name.setText(items.get(position).nameF);
        holder.size.setText(items.get(position).itemF);

        if (items.get(position).nameF.endsWith(".docx")){
            position_image.add(R.drawable.docx_win);
        }else if (items.get(position).nameF.endsWith(".pdf")){
            position_image.add(R.drawable.pdf);
        }else if (items.get(position).nameF.endsWith(".pptx")){
            position_image.add(R.drawable.pptx_win);
        }else if (items.get(position).nameF.endsWith(".rar")){
            position_image.add(R.drawable.rar);
        }else if (items.get(position).nameF.endsWith(".txt")){
            position_image.add(R.drawable.text);
        }else if (items.get(position).nameF.endsWith(".zip")){
            position_image.add(R.drawable.zip);
        }else {
            position_image.add(R.drawable.unknown);
        }

        holder.icon.setImageResource(position_image.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

   private class DownMissingApps extends AsyncTask<String, Integer, String> {
       private Context context;
       private PowerManager.WakeLock mWL;
       private ProgressDialog mPD;
       private boolean success;
       private File file = new File("");

    DownMissingApps(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //prevent power button from interrupting download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWL = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWL.acquire();
        mPD = new ProgressDialog(context);
        mPD.setMessage(context.getString(R.string.downloading));
        mPD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPD.setCancelable(true);
        mPD.setCanceledOnTouchOutside(false);

        mPD.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        mPD.show();
    }


    @Override
    protected String doInBackground(String... sUrl) {
        InputStream is;
        OutputStream os;
        HttpURLConnection conn;

        while (!isCancelled()) {
            try {
                URL url = new URL(sUrl[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                //wait for server response 200 so we not get a wrong file
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return context.getString(R.string.httperr);
                }

                //file size
                int fileLength = conn.getContentLength();

                //Dzownload the file
                is = conn.getInputStream();
                file = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + nameOfAPK);

                if (file.length() == fileLength) {
                    success = true;
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
                    success = true;
                    return context.getString(R.string.dwncmp);
                }

            } catch (Exception e) {
                file.delete();
                success = false;
                return context.getString(R.string.dwnfld);
            }
        }
        return context.getString(R.string.dwncncl);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mPD.setMax(100);
        mPD.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        mWL.release();
        mPD.dismiss();
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        if (success){
            try {
                Intent installApk = new Intent(Intent.ACTION_VIEW);
                installApk.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(installApk);
            }catch (ActivityNotFoundException ex){
                Toast.makeText(context, context.getString(R.string.erroccur),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        Toast.makeText(context, context.getString(R.string.err) + " " + s, Toast.LENGTH_LONG).show();
    }
    }

}
