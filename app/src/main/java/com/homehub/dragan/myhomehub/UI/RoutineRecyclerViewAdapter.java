package com.homehub.dragan.myhomehub.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Activities.AutomationActivity;
import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.Classes.model.Routine;
import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class RoutineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ViewGroup vg;
    private List<Routine> items;
    private Context mContext;

    @Override
    public int getItemCount() { return this.items.size(); }

    public RoutineRecyclerViewAdapter(List<Routine> items, Context context) {

        this.items = items;
        this.mContext = context;
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
                Log.d("Routine deleted",String.valueOf(i));
                if (RoutineList.getInstance().routines.isEmpty()) {
                    makeInvisible(vg);
                }
            }
        });

        rvh1.btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mContext instanceof AutomationActivity) {
                    RoutineList.getInstance().selectedRoutinePosition = i; // use this rather than setSelectedRoutinePosition(i)
                    ((AutomationActivity)mContext).editButtonPressed(); // goes to EditRoutineActivity

                } else {
                    // else should never happen, just added it for fun
                    Toast.makeText(mContext, "Error: RoutineRecyclerViewAdapter should not be used in any activity other than AutomationActivity", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void configureRoutineViewHolder(RoutineViewHolder rvh1, int position) {
        Routine routine = (Routine) items.get(position);
        rvh1.getDeviceName().setText(routine.getDeviceName());
        rvh1.getActionTv().setText(routine.getAction());

        if (routine.getTrigger().getIsTriggerOnDeviceAction() == false) { // time-based
            rvh1.getActivatorTv().setText("At " + routine.getActivatorName() + ", ");
        } else if (routine.getTrigger().getIsTriggerOnDeviceAction() == true) { // action-based
            rvh1.getActivatorTv().setText("Upon " + routine.getTriggerDevice().getFriendlyName() + " " + routine.getTriggerAction() + ", ");
        }
    }

    public void removeItem(int i) {
        Object theRemovedItem = RoutineList.getInstance().routines.get(i);
        // remove your item from data base
        RoutineList.getInstance().routines.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, getItemCount()); // this prevents app from crashing when routines are deleted in incorrect order

    }
}
