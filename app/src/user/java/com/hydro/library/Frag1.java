package com.hydro.library;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class Frag1 extends Fragment {

    Adapter adap;
    RecyclerView rv;
    TextView nFound, txt;
    EmptyAdapter adapter = new EmptyAdapter(null);

    public Frag1() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.fragment_frag1, container, false);

        if (getResources().getBoolean(R.bool.is_right_to_left))
            rView.setRotationY(180);

        nFound = rView.findViewById(R.id.nFound1);
        txt = rView.findViewById(R.id.searchfirst);
        txt.setVisibility(View.VISIBLE);
        rv = rView.findViewById(R.id.rec_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        return rView;
    }



     public void updateR(ArrayList<listItem> res) {

         adap = new Adapter(res);
         rv.setAdapter(adap);
         if (adap.getItemCount() == 0) {
             nFound.setVisibility(View.VISIBLE);
         } else {
             nFound.setVisibility(View.GONE);
         }

         txt.setVisibility(View.GONE);
         ((MainActivity) getActivity()).getPager().setCurrentItem(1);

    }


}
