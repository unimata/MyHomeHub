package com.homehub.dragan.myhomehub.UI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homehub.dragan.myhomehub.Activities.AutomationActivity;
import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class RoutineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ViewGroup vg;

    private List<Routine> items;

    @Override
    public int getItemCount() { return this.items.size(); }

    public void removeItem(int i) {
        Object theRemovedItem = RoutineList.getInstance().routines.get(i);
        // remove your item from data base
        RoutineList.getInstance().routines.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, getItemCount()); // this prevents app from crashing when routines are deleted in incorrect order
        //TODO: find a way to make RecyclerView invisible when list is emptied

    }

    public RoutineRecyclerViewAdapter(List<Routine> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1;

        vg = viewGroup;

        v1 = inflater.inflate(R.layout.routine_viewholder_relative, viewGroup, false);

        viewHolder = new RoutineViewHolder(v1);

        //viewGroup.setVisibility(View.VISIBLE); // this works



        return viewHolder;
    }

    // makes the RecyclerView invisible when RoutineList is empty
    public void makeInvisible(ViewGroup viewGroup) {
        viewGroup.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        RoutineViewHolder rvh1 = (RoutineViewHolder) viewHolder;
        configureRoutineViewHolder(rvh1, i);
        rvh1.btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeItem(i);
                if (RoutineList.getInstance().routines.isEmpty()) {
                    makeInvisible(vg);
                }
            }
        });
    }

    private void configureRoutineViewHolder(RoutineViewHolder rvh1, int position) {
        Routine routine = (Routine) items.get(position);
        rvh1.getDeviceName().setText(routine.getDeviceName());
        rvh1.getActionTv().setText(routine.getAction());
        rvh1.getActivatorTv().setText("At " + routine.getActivator() + ", ");
    }
}
