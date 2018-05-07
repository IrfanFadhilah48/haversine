package com.example.windowsv8.haversine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Windowsv8 on 22/04/2018.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context context;
    ArrayList<Jarak> jaraks;

    public CardAdapter(ArrayList<Jarak> jaraks1, Context context){
        super();
        this.jaraks = jaraks1;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_recycler,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Jarak jarak = jaraks.get(position);

        holder.textviewnama.setText(jarak.getNama());
        holder.textViewjarak.setText(jarak.getJarak());
    }

    @Override
    public int getItemCount() {
        return jaraks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textviewnama, textViewjarak, textViewtesta;

        public ViewHolder(View itemView) {
            super(itemView);
            textviewnama = itemView.findViewById(R.id.nama);
            textViewjarak = itemView.findViewById(R.id.jarak);
            textViewtesta = itemView.findViewById(R.id.testa);
        }
    }
}
