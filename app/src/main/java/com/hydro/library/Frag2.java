package com.hydro.library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;




public class Frag2 extends Fragment {
    Button refresh, lol;
    RecyclerView rec_view;
    Adapter2 adap;
    TextView nFound2;
    ArrayList<listItem> items = new ArrayList<>();
    String subject = "OS", subtype = "M";
    final Adapter2 delPath = new Adapter2();
    boolean isVisibleToUser;

    public Frag2() {
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            addFilesTolistview(subject + File.separator + subtype);
            delPath.setPath(subject + File.separator + subtype);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rec_view = getActivity().findViewById(R.id.rec_view2);
        refresh = getActivity().findViewById(R.id.refresh);
        nFound2 = getActivity().findViewById(R.id.nFound2);
        lol = getActivity().findViewById(R.id.lol);

        lol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp(v);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setRepeatCount(0);
                animation.setDuration(1000);
                refresh.setAnimation(animation);
                addFilesTolistview(subject + File.separator + subtype);

            }
        });



    }

    private void adb() {
        final CharSequence[] subtypes = {getString(R.string.Lec), getString(R.string.lab), getString(R.string.exam), getString(R.string.oth)};
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setSingleChoiceItems(subtypes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        subtype = "M";
                        addFilesTolistview(subject + File.separator + subtype);
                        delPath.setPath(subject + File.separator + subtype);
                        break;
                    case 1:
                        subtype = "L";
                        addFilesTolistview(subject + File.separator + subtype);
                        delPath.setPath(subject + File.separator + subtype);
                        break;
                    case 2:
                        subtype = "E";
                        addFilesTolistview(subject + File.separator + subtype);
                        delPath.setPath(subject + File.separator + subtype);
                        break;
                    case 3:
                        subtype = "O";
                        addFilesTolistview(subject + File.separator + subtype);
                        delPath.setPath(subject + File.separator + subtype);
                        break;
                }
            }
        });
        adb.show();
    }

    private void popUp(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.pop, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.os:
                        subject = "OS";
                        adb();
                        break;
                    case R.id.db:
                        subject = "DB";
                        adb();
                        break;
                    case R.id.cg:
                        subject = "CG";
                        adb();
                        break;
                    case R.id.cn:
                        subject = "CN";
                        adb();
                        break;
                    case R.id.ooad:
                        subject = "OOAD";
                        adb();
                        break;
                    case R.id.oop:
                        subject = "OOP";
                        adb();
                        break;
                    case R.id.ser:
                        subject = "SER";
                        adb();
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserVisibleHint(false);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.fragment_frag2, container, false);



        if (getResources().getBoolean(R.bool.is_right_to_left))
            rView.setRotationY(180);


        return rView;
    }

    private void addFilesTolistview(String path) {
        items.clear();
        File f2 = new File(Environment.getExternalStorageDirectory() + File.separator + "SEL" + File.separator + path);
        File[] l2 = f2.listFiles();
        if (l2 != null) {
            for (File file : l2) {
                double size = file.length();
                if (size >= 1048576) {
                    items.add(new listItem(file.getName(), String.format("Size %.2f", size / 1048576) + " MB"));
                } else if (size >= 1024) {
                    items.add(new listItem(file.getName(), String.format("Size %.2f", size / 1024) + " KB"));
                } else if (size > 1) {
                    items.add(new listItem(file.getName(), "Size " + size + " bytes"));
                } else if (size == 1) {
                    items.add(new listItem(file.getName(), "Size " + size + " byte"));
                } else {
                    items.add(new listItem(file.getName(), "0 bytes"));
                }
            }

            if (items.size() == 0) {
                nFound2.setVisibility(View.VISIBLE);
            } else {
                nFound2.setVisibility(View.GONE);
            }


            adap = new Adapter2(items);
            adap.setTxt(nFound2);
            rec_view.setLayoutManager(new LinearLayoutManager(rec_view.getContext()));
            rec_view.setItemAnimator(new DefaultItemAnimator());
            rec_view.setAdapter(adap);
        }

    }
}
