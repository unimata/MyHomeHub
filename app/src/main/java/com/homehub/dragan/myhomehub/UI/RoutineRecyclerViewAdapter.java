package com.homehub.dragan.myhomehub.UI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class RoutineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;

    @Override
    public int getItemCount() { return this.items.size(); }

    public RoutineRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1;

        v1 = inflater.inflate(R.layout.routine_viewholder_relative, viewGroup, false);

        viewHolder = new RoutineViewHolder(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RoutineViewHolder rvh1 = (RoutineViewHolder) viewHolder;
        configureRoutineViewHolder(rvh1, i);
    }

    private void configureRoutineViewHolder(RoutineViewHolder rvh1, int position) {
        Routine routine = (Routine) items.get(position);
        rvh1.getDeviceName().setText(routine.getDeviceName());
        rvh1.getActionTv().setText(routine.getAction());
        rvh1.getActivatorTv().setText("At " + routine.getActivator() + ", ");
    }

}
