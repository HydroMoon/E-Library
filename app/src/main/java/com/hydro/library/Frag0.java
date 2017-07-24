package com.hydro.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Frag0 extends Fragment {
    public static String subject;
    public static String subtype;
    private String fPath = "";
    private String fin = "";
    public static String uploadPath = "";
    private String url = "http://inscrutable-sixes.000webhostapp.com/listfiles.php?p1=";
    ConnectInternet conTest;
    private inter comm = null;
    Button btn;
    Spinner spin, spin2;
    ArrayList<listItem> d_list = new ArrayList<>();
    private int NextTip = 0;
    private TextView textView;
    private ArrayList<Integer> RandomNumber = new ArrayList<>();

    public Frag0() {
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn = getActivity().findViewById(R.id.button);
        spin = getActivity().findViewById(R.id.spinner);
        spin2 = getActivity().findViewById(R.id.spinner2);
        textView = getActivity().findViewById(R.id.textView);
        comm = (inter) getActivity();
        //img = (ImageView) getActivity().findViewById(R.id.imgfose);

        MakeRandomNumber(RandomNumber);
        TipOfToday(getActivity(),RandomNumber.get(0));

        SpinnerInit();


            textView.setOnTouchListener(new OnswipeTouchListener(getActivity()) {
                public void SwipeUP() {
                    NextTip++;
                    if (NextTip > RandomNumber.size() - 1) {
                        Collections.shuffle(RandomNumber);
                        NextTip = 0;
                    }
                    TipOfToday(getActivity(), RandomNumber.get(NextTip));
                }

                public void SwipeButton() {
                    NextTip--;
                    if (NextTip < 0) {
                        Collections.shuffle(RandomNumber);
                        NextTip = RandomNumber.size();
                    }
                    TipOfToday(getActivity(), RandomNumber.get(NextTip));
                }

                public void onLongClick() {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(TipOfToday(getActivity(), RandomNumber.get(NextTip)))
                            .setPositiveButton("Ok", null)
                            .create()
                            .show();
                }
            });


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            subject = "OS";
                            fPath = subject;
                            break;
                        case 1:
                            subject = "DB";
                            fPath = subject;
                            break;
                        case 2:
                            subject = "CG";
                            fPath = subject;
                            break;
                        case 3:
                            subject = "NET";
                            fPath = subject;
                            break;
                        case 4:
                            subject = "OOAD";
                            fPath = subject;
                            break;
                        case 5:
                            subject = "OOP";
                            fPath = subject;
                            break;
                        case 6:
                            subject = "SER";
                            fPath = subject;
                            break;
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        subtype = "M";
                        break;
                    case 1:
                        subtype = "L";
                        break;
                    case 2:
                        subtype = "E";
                        break;
                    case 3:
                        subtype = "O";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conTest = new ConnectInternet(getActivity());
                fin = url + fPath + "&p2=" + subtype;

                uploadPath = File.separator + subject + File.separator + subtype + File.separator;


                Adapter test = new Adapter();
                test.download(uploadPath, uploadPath);



                if (conTest.is_connected()) {
                    new ParseData(getContext()).execute(fin);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.httperr), Toast.LENGTH_LONG).show();
                }
            }
        }) ;
    }

    public String getUploadPath() {
        uploadPath = File.separator + subject + File.separator + subtype + File.separator;
        String nUploadPath = subject + "&" + subtype;
        return uploadPath + "<>" + nUploadPath;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.fragment_frag0, container, false);

        if (getResources().getBoolean(R.bool.is_right_to_left))
            rView.setRotationY(180);



        return rView;
    }

    public class ParseData extends AsyncTask<String, Void, String> {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        private ProgressDialog mPD;
        private Context ctx;

        private ParseData(Context context) {
            this.ctx = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPD = new ProgressDialog(ctx);
            mPD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPD.setMessage(ctx.getString(R.string.fetch));
            mPD.setCanceledOnTouchOutside(false);
            mPD.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                InputStream is = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                StringBuilder buf = new StringBuilder();

                String str;

                while ((str = br.readLine()) != null) {
                    buf.append(str);
                }
                br.close();
                Log.i("JSON: ", buf.toString());

                return buf.toString();
            } catch (SocketTimeoutException ex) {
                return ex.toString();
            }  catch (IOException e) {
                Log.e("NETinBG: ", e.toString());
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JsonParser(s);
                mPD.dismiss();
            } catch (Exception e) {
                Log.e("NETinPE: ", e.toString());
                Toast.makeText(getActivity(), getString(R.string.httperr), Toast.LENGTH_LONG).show();
            }

        }

        void JsonParser(String jsonString) {
            JSONObject json;
            try {
                json = new JSONObject(jsonString);
                JSONArray jarr = json.getJSONArray("nsarray");
                d_list.clear();
                for (int x = 0; x < jarr.length(); x++) {
                    JSONObject jsonObject = jarr.getJSONObject(x);
                    String name = jsonObject.getString("name");
                    String size = jsonObject.getString("size");
                    d_list.add(new listItem(name, "Size " + size));
                }

                sender(d_list, uploadPath);
            } catch (JSONException e) {
                Log.e("JSON", e.toString());
                d_list.clear();
                sender(d_list, uploadPath);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            comm = (inter) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement interface");
        }
    }

    public void sender(ArrayList<listItem> res, String upPath) {
        comm.sData(res, upPath);
    }


    @Override
    public void onDetach() {
        comm = null;
        super.onDetach();
    }

    public void SpinnerInit() {
        //spinner 1
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.OS));
        list.add(getString(R.string.DB));
        list.add(getString(R.string.CG));
        list.add(getString(R.string.CN));
        list.add(getString(R.string.OOAD));
        list.add(getString(R.string.OOP));
        list.add(getString(R.string.SR));
        ArrayAdapter<String> dAdap = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, list);
        dAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dAdap);

        //spinner 2
        List<String> list2 = new ArrayList<>();
        list2.add(getString(R.string.Lec));
        list2.add(getString(R.string.lab));
        list2.add(getString(R.string.exam));
        list2.add(getString(R.string.oth));
        ArrayAdapter<String> dAdap2 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, list2);
        dAdap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(dAdap2);
    }

    private Integer MakeRandomNumber(ArrayList<Integer> RandomNumber) {
        for (int i = 1; i <=101; i++) {
            RandomNumber.add(i);
        }
        Collections.shuffle(RandomNumber);
        return RandomNumber.get(0);
    }

    private String TipOfToday(Context context,int RandomNumber) {
        try {
            InputStream InputStream = context.getAssets().open("tips.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(InputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line,numberBefore,numberAfter;
            byte numberLength = 2;
            numberAfter = "<" + String.valueOf(RandomNumber) + ">";
            if (RandomNumber <= 9) {
                numberLength += 1;
            }else if (RandomNumber <= 99) {
                numberLength += 2;
            } else{
                numberLength += 3;
            }
            while ((line = bufferedReader.readLine()) !=null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                if (line.contains("<" + RandomNumber +">")) {
                    RandomNumber--;
                    break;
                }
            }
            inputStreamReader.close();
            numberBefore = "<" +  String.valueOf(RandomNumber) + ">";

            textView.setSingleLine(false);
            textView.setText(stringBuilder.substring(stringBuilder.indexOf(numberBefore) + numberLength, stringBuilder.indexOf(numberAfter)));

            return stringBuilder.substring(stringBuilder.indexOf(numberBefore) + numberLength , stringBuilder.indexOf(numberAfter));
        } catch (FileNotFoundException e) {
            Toast.makeText(context,"File not found",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context,"Can't Read",Toast.LENGTH_LONG).show();
        }
        finally {
            try {
                setTextAfterEllipsis(textView, 5, "{اضغط مطولا لعرض المزيد}");
                textView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            } catch (Resources.NotFoundException ex) {
                Toast.makeText(getActivity(), "Resource not found", Toast.LENGTH_SHORT).show();
            }
        }

        return "Err While Getting Tips Please Try Again";
    }

    private void setTextAfterEllipsis(final TextView textView, final int maxLines, final String expandText) {
        if (textView.getTag() == null) {
            textView.setTag(textView.getText());
        }
        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = textView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                }
                if (textView.getLineCount() > maxLines) {
                    int lineEndIndex = textView.getLayout().getLineEnd(maxLines);
                    String text = textView.getText().subSequence(0, lineEndIndex) + "\n" + expandText;
                    textView.setText(text);
                }
            }
        });
    }


}
