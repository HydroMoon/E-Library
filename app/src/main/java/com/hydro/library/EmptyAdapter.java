package com.hydro.library;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



class EmptyAdapter extends RecyclerView.Adapter <EmptyAdapter.ViewHolder> {
    private String message;

    EmptyAdapter(String message) {
        this.message = message;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public EmptyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emptyview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if (message != null) {
            viewHolder.msg.setText(message);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView msg;

        ViewHolder(View view) {
            super(view);
            mView = view;
            msg = view.findViewById(R.id.empty);
        }

    }
}
